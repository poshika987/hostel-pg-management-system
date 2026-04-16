package com.hostel.management_system.repository;

import com.hostel.management_system.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AllocationRepository extends JpaRepository<Allocation, Long> {

    Optional<Allocation> findByStudentAndStatus(Student student, AllocationStatus status);

    Optional<Allocation> findByStudentIdAndStatus(Long studentId, AllocationStatus status);
}
