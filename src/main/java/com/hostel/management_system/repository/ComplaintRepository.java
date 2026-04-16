package com.hostel.management_system.repository;

import com.hostel.management_system.model.Complaint;
import com.hostel.management_system.model.ComplaintStatus;
import com.hostel.management_system.model.MaintenanceStaff;
import com.hostel.management_system.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStudentOrderByCreatedAtDesc(Student student);
    List<Complaint> findAllByOrderByCreatedAtDesc();
    long countByStatus(ComplaintStatus status);
    long countByAssignedStaffAndStatusNot(MaintenanceStaff assignedStaff, ComplaintStatus status);
}
