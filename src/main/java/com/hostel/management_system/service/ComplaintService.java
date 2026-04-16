package com.hostel.management_system.service;

import com.hostel.management_system.exception.ResourceNotFoundException;
import com.hostel.management_system.model.*;
import com.hostel.management_system.repository.ComplaintRepository;
import com.hostel.management_system.repository.MaintenanceStaffRepository;
import com.hostel.management_system.service.observer.ComplaintStatusObserver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final MaintenanceStaffRepository maintenanceStaffRepository;
    private final AllocationService allocationService;
    private final INotificationService notificationService;
    private final List<ComplaintStatusObserver> observers;
    private final Path uploadRoot = Path.of("uploads", "complaints");

    public ComplaintService(ComplaintRepository complaintRepository,
                            MaintenanceStaffRepository maintenanceStaffRepository,
                            AllocationService allocationService,
                            INotificationService notificationService,
                            List<ComplaintStatusObserver> observers) {
        this.complaintRepository = complaintRepository;
        this.maintenanceStaffRepository = maintenanceStaffRepository;
        this.allocationService = allocationService;
        this.notificationService = notificationService;
        this.observers = observers;
    }

    @Transactional
    public Complaint raiseComplaint(Student student,
                                    String title,
                                    String category,
                                    String description,
                                    ComplaintPriority priority,
                                    MultipartFile photo) {
        Room room = allocationService.getActiveAllocation(student)
                .map(Allocation::getRoom)
                .orElse(null);

        Complaint complaint = Complaint.builder(student, clean(title), clean(category), clean(description))
                .room(room)
                .priority(priority)
                .build();
        complaint.addTimelineEntry(ComplaintStatus.PENDING, "Complaint raised by student.");
        storePhotoIfPresent(complaint, photo);

        Complaint saved = complaintRepository.save(complaint);
        notificationService.notify(student, "Complaint #" + saved.getComplaintId() + " has been submitted.");
        return saved;
    }

    @Transactional
    public Complaint assignComplaint(Long complaintId, Long staffId) {
        Complaint complaint = getComplaint(complaintId);
        MaintenanceStaff staff = maintenanceStaffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance staff not found: " + staffId));
        ComplaintStatus oldStatus = complaint.getStatus();
        complaint.assignTo(staff);
        complaint.addTimelineEntry(ComplaintStatus.ASSIGNED, "Assigned to " + staff.getName() + ".");
        Complaint saved = complaintRepository.save(complaint);
        notifyObservers(saved, oldStatus, "Assigned to " + staff.getName() + ".");
        return saved;
    }

    @Transactional
    public Complaint updateStatus(Long complaintId, ComplaintStatus status, String note) {
        Complaint complaint = getComplaint(complaintId);
        ComplaintStatus oldStatus = complaint.getStatus();
        complaint.changeStatus(status);
        complaint.addTimelineEntry(status, clean(note));
        Complaint saved = complaintRepository.save(complaint);
        notifyObservers(saved, oldStatus, clean(note));
        return saved;
    }

    @Transactional
    public Complaint reopenComplaint(Long complaintId, Student student, String reason) {
        Complaint complaint = getComplaint(complaintId);
        if (!complaint.getStudent().getId().equals(student.getId())) {
            throw new IllegalStateException("You can reopen only your own complaint.");
        }
        if (complaint.getStatus() != ComplaintStatus.CLOSED) {
            throw new IllegalStateException("Only closed complaints can be reopened.");
        }

        ComplaintStatus oldStatus = complaint.getStatus();
        String reopenNote = clean(reason).isBlank()
                ? "Student reopened the complaint because the resolution was not satisfactory."
                : "Student reopened the complaint: " + clean(reason);
        complaint.changeStatus(ComplaintStatus.REOPENED);
        complaint.addTimelineEntry(ComplaintStatus.REOPENED, reopenNote);
        Complaint saved = complaintRepository.save(complaint);
        notifyObservers(saved, oldStatus, reopenNote);
        return saved;
    }

    public Complaint getComplaint(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found: " + id));
    }

    public List<Complaint> getComplaintsFor(Student student) {
        return complaintRepository.findByStudentOrderByCreatedAtDesc(student);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<MaintenanceStaff> getMaintenanceStaff() {
        return maintenanceStaffRepository.findAll();
    }

    public long countOpenComplaints() {
        return getAllComplaints().stream()
                .filter(c -> c.getStatus() != ComplaintStatus.CLOSED)
                .count();
    }

    private void notifyObservers(Complaint complaint, ComplaintStatus oldStatus, String note) {
        if (oldStatus == complaint.getStatus()) {
            return;
        }
        observers.forEach(observer -> observer.onStatusChanged(complaint, oldStatus, note));
    }

    private void storePhotoIfPresent(Complaint complaint, MultipartFile photo) {
        if (photo == null || photo.isEmpty()) {
            return;
        }
        try {
            Files.createDirectories(uploadRoot);
            String originalName = StringUtils.cleanPath(photo.getOriginalFilename() == null ? "complaint-photo" : photo.getOriginalFilename());
            String extension = "";
            int dotIndex = originalName.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = originalName.substring(dotIndex);
            }
            String storedName = UUID.randomUUID() + extension;
            Path destination = uploadRoot.resolve(storedName).normalize();
            Files.copy(photo.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            complaint.addPhoto("/complaint-photos/" + storedName, originalName);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store complaint photo.", ex);
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
