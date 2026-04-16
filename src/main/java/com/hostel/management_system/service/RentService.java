package com.hostel.management_system.service;

import com.hostel.management_system.exception.ResourceNotFoundException;
import com.hostel.management_system.model.AllocationStatus;
import com.hostel.management_system.model.RentPolicy;
import com.hostel.management_system.model.Student;
import com.hostel.management_system.repository.AllocationRepository;
import com.hostel.management_system.repository.RentPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * GRASP – Information Expert: RentService owns all rent calculation logic.
 */
@Service
public class RentService {

    @Autowired
    private RentPolicyRepository policyRepo;
    @Autowired
    private AllocationRepository allocationRepository;

    private RentPolicy getPolicy() {
        return policyRepo.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No rent policy configured. Please seed the rent_policies table."));
    }

    public double calculateRent() {
        return getPolicy().getMonthlyRent();
    }

    public double calculateRent(Student student) {
        return allocationRepository.findByStudentAndStatus(student, AllocationStatus.ACTIVE)
                .map(allocation -> allocation.getRoom().getPrice())
                .orElse(0.0);
    }

    public double calculateRent(Long studentId) {
        return getAllocatedRoomRent(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active room allocation found for student " + studentId + "."));
    }

    public Optional<Double> getAllocatedRoomRent(Long studentId) {
        return allocationRepository.findByStudentIdAndStatus(studentId, AllocationStatus.ACTIVE)
                .map(allocation -> allocation.getRoom().getPrice())
                .map(Double::valueOf);
    }

    public double getLateFee() {
        return 100.0;
    }

    public double calculateLateFee(LocalDate dueDate) {
        LocalDate graceEnds = dueDate.plusDays(5);
        LocalDate today = LocalDate.now();

        if (!today.isAfter(graceEnds)) {
            return 0.0;
        }

        long lateDays = ChronoUnit.DAYS.between(graceEnds, today);
        return lateDays * getLateFee();
    }
}
