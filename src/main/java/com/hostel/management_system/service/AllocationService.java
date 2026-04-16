package com.hostel.management_system.service;

import com.hostel.management_system.exception.ResourceNotFoundException;
import com.hostel.management_system.model.*;
import com.hostel.management_system.repository.AllocationRepository;
import com.hostel.management_system.service.strategy.AllocationStrategy;
import com.hostel.management_system.service.strategy.FirstAvailableStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Behavioral Pattern – Strategy:
 *   Uses AllocationStrategy to select a room from candidates.
 *   Swap FirstAvailableStrategy for e.g. PriorityBasedStrategy at runtime.
 *
 * SOLID – SRP: only manages allocation records & room occupancy transitions.
 */
@Service
public class AllocationService {

    @Autowired private AllocationRepository allocationRepository;
    @Autowired private RoomService roomService;

    // Strategy Pattern — swap without touching this class
    private final AllocationStrategy strategy = new FirstAvailableStrategy();

    @Transactional
    public Allocation allocateRoom(Student student, Room targetRoom) {
        if (allocationRepository.findByStudentAndStatus(student, AllocationStatus.ACTIVE).isPresent()) {
            throw new IllegalStateException("Student already has an active room allocation.");
        }

        Room room = strategy.allocate(List.of(targetRoom));
        if (room == null) {
            throw new ResourceNotFoundException(
                    "Room " + targetRoom.getRoomId() + " is no longer available.");
        }

        Allocation allocation = new Allocation();
        allocation.setStudent(student);
        allocation.setRoom(room);
        allocation.setAllocatedAt(LocalDateTime.now());
        allocation.setStatus(AllocationStatus.ACTIVE);

        roomService.markOccupied(room);
        return allocationRepository.save(allocation);
    }

    @Transactional
    public void vacateRoom(Student student) {
        Allocation allocation = allocationRepository
                .findByStudentAndStatus(student, AllocationStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active room allocation found for this student."));

        allocation.vacate();
        roomService.markVacated(allocation.getRoom());
        allocationRepository.save(allocation);
    }

    public Optional<Allocation> getActiveAllocation(Student student) {
        return allocationRepository.findByStudentAndStatus(student, AllocationStatus.ACTIVE);
    }

    public List<Allocation> getAllAllocations() {
        return allocationRepository.findAll();
    }

    public long countActive() {
        return allocationRepository.findAll().stream()
                .filter(a -> a.getStatus() == AllocationStatus.ACTIVE).count();
    }
}
