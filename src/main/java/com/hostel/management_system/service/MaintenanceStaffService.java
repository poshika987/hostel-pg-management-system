package com.hostel.management_system.service;

import com.hostel.management_system.exception.ResourceNotFoundException;
import com.hostel.management_system.model.ComplaintStatus;
import com.hostel.management_system.model.MaintenanceStaff;
import com.hostel.management_system.repository.ComplaintRepository;
import com.hostel.management_system.repository.MaintenanceStaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MaintenanceStaffService {

    private final MaintenanceStaffRepository staffRepository;
    private final ComplaintRepository complaintRepository;

    public MaintenanceStaffService(MaintenanceStaffRepository staffRepository,
                                   ComplaintRepository complaintRepository) {
        this.staffRepository = staffRepository;
        this.complaintRepository = complaintRepository;
    }

    public List<MaintenanceStaff> getAllStaff() {
        return staffRepository.findAll();
    }

    public MaintenanceStaff getStaff(Long staffId) {
        return staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance staff not found: " + staffId));
    }

    @Transactional
    public MaintenanceStaff save(MaintenanceStaff staff) {
        staff.setName(clean(staff.getName()));
        staff.setRole(clean(staff.getRole()));
        staff.setContact(clean(staff.getContact()));
        return staffRepository.save(staff);
    }

    @Transactional
    public void deleteStaff(Long staffId) {
        MaintenanceStaff staff = getStaff(staffId);
        long activeTasks = complaintRepository.countByAssignedStaffAndStatusNot(staff, ComplaintStatus.CLOSED);
        if (activeTasks > 0) {
            throw new IllegalStateException("Cannot delete staff assigned to active complaints.");
        }
        staffRepository.delete(staff);
    }

    public long countActiveTasks(MaintenanceStaff staff) {
        return complaintRepository.countByAssignedStaffAndStatusNot(staff, ComplaintStatus.CLOSED);
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
