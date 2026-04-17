package com.hostel.management_system.service;

import com.hostel.management_system.exception.ResourceNotFoundException;
import com.hostel.management_system.model.*;
import com.hostel.management_system.repository.RoomActionRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoomActionRequestService {

    private final RoomActionRequestRepository requestRepository;
    private final AllocationService allocationService;
    private final RoomService roomService;

    public RoomActionRequestService(RoomActionRequestRepository requestRepository,
                                    AllocationService allocationService,
                                    RoomService roomService) {
        this.requestRepository = requestRepository;
        this.allocationService = allocationService;
        this.roomService = roomService;
    }

    @Transactional
    public RoomActionRequest requestVacate(Student student) {
        Allocation allocation = allocationService.getActiveAllocation(student)
                .orElseThrow(() -> new ResourceNotFoundException("No active room allocation found for this student."));
        ensureNoPendingRequest(student);
        RoomActionRequest request = new RoomActionRequest();
        request.setStudent(student);
        request.setCurrentRoom(allocation.getRoom());
        request.setType(RoomActionType.VACATE_ROOM);
        request.setRequestDate(LocalDateTime.now());
        return requestRepository.save(request);
    }

    @Transactional
    public RoomActionRequest requestRoomChange(Student student, Integer newRoomId) {
        Allocation allocation = allocationService.getActiveAllocation(student)
                .orElseThrow(() -> new ResourceNotFoundException("You need an active allocation before requesting a room change."));
        Room requestedRoom = roomService.getRoomById(newRoomId);
        if (!requestedRoom.isAvailable()) {
            throw new IllegalStateException("Selected room is no longer available.");
        }
        ensureNoPendingRequest(student);
        RoomActionRequest request = new RoomActionRequest();
        request.setStudent(student);
        request.setCurrentRoom(allocation.getRoom());
        request.setRequestedRoom(requestedRoom);
        request.setType(RoomActionType.CHANGE_ROOM);
        request.setRequestDate(LocalDateTime.now());
        return requestRepository.save(request);
    }

    @Transactional
    public void approve(Long requestId) {
        RoomActionRequest request = getRequest(requestId);
        if (request.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending room requests can be approved.");
        }
        if (request.getType() == RoomActionType.VACATE_ROOM) {
            allocationService.vacateRoom(request.getStudent());
        } else {
            if (request.getRequestedRoom() == null || !request.getRequestedRoom().isAvailable()) {
                throw new IllegalStateException("Requested room is no longer available.");
            }
            allocationService.changeRoom(request.getStudent(), request.getRequestedRoom());
        }
        request.setStatus(BookingStatus.APPROVED);
        requestRepository.save(request);
    }

    @Transactional
    public void reject(Long requestId) {
        RoomActionRequest request = getRequest(requestId);
        if (request.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending room requests can be rejected.");
        }
        request.setStatus(BookingStatus.REJECTED);
        requestRepository.save(request);
    }

    public List<RoomActionRequest> getPendingRequests() {
        return requestRepository.findByStatusOrderByRequestDateDesc(BookingStatus.PENDING);
    }

    public List<RoomActionRequest> getAllRequests() {
        return requestRepository.findAllByOrderByRequestDateDesc();
    }

    public List<RoomActionRequest> getRequestsFor(Student student) {
        return requestRepository.findByStudentOrderByRequestDateDesc(student);
    }

    private RoomActionRequest getRequest(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Room request not found: " + requestId));
    }

    private void ensureNoPendingRequest(Student student) {
        if (requestRepository.existsByStudentAndStatus(student, BookingStatus.PENDING)) {
            throw new IllegalStateException("You already have a pending room change or vacate request.");
        }
    }
}
