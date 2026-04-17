package com.hostel.management_system.facade;

import com.hostel.management_system.model.*;
import com.hostel.management_system.service.ComplaintService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class ComplaintSystemFacade {

    private final ComplaintService complaintService;

    public ComplaintSystemFacade(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    public Complaint raiseComplaint(Student student,
                                    String title,
                                    String category,
                                    String description,
                                    ComplaintPriority priority,
                                    MultipartFile photo) {
        return complaintService.raiseComplaint(student, title, category, description, priority, photo);
    }

    public List<Complaint> viewComplaintStatus(Student student) {
        return complaintService.getComplaintsFor(student);
    }

    public List<Complaint> viewAllComplaints() {
        return complaintService.getAllComplaints();
    }

    public Complaint assignTaskToMaintenanceStaff(Long complaintId, Long staffId) {
        return complaintService.assignComplaint(complaintId, staffId);
    }

    public Complaint updateRepairStatus(Long complaintId, ComplaintStatus status, String note) {
        return complaintService.updateStatus(complaintId, status, note);
    }

    public Complaint updateRepairStatusByMaintenanceStaff(Long complaintId,
                                                          MaintenanceStaff staff,
                                                          ComplaintStatus status,
                                                          String note) {
        return complaintService.updateStatusByMaintenanceStaff(complaintId, staff, status, note);
    }

    public List<Complaint> viewAssignedComplaints(MaintenanceStaff staff) {
        return complaintService.getComplaintsAssignedTo(staff);
    }

    public Complaint reopenComplaint(Student student, Long complaintId, String reason) {
        return complaintService.reopenComplaint(complaintId, student, reason);
    }

    public List<MaintenanceStaff> getMaintenanceStaff() {
        return complaintService.getMaintenanceStaff();
    }

    public long countOpenComplaints() {
        return complaintService.countOpenComplaints();
    }
}
