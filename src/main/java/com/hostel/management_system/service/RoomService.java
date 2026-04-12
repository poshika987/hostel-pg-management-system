package com.hostel.management_system.service;

import com.hostel.management_system.exception.ResourceNotFoundException;
import com.hostel.management_system.model.*;
import com.hostel.management_system.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SOLID – SRP: Handles only room-related read/write operations.
 * GRASP – Information Expert: Delegates occupy/release to Room itself.
 */
@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    /** Returns ALL rooms (available + full + maintenance) for the browse page. */
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    /** Returns only rooms with at least one free slot. */
    public List<Room> getAvailableRooms() {
        return roomRepository.findAll()
                .stream()
                .filter(Room::isAvailable)
                .toList();
    }

    public Room getRoomById(Integer id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + id));
    }

    /**
     * Called after a booking is approved.
     * Delegates state mutation to Room (GRASP – Information Expert).
     */
    public void markOccupied(Room room) {
        room.occupy();
        roomRepository.save(room);
    }

    /**
     * Called when a student vacates.
     * Delegates state mutation to Room (GRASP – Information Expert).
     */
    public void markVacated(Room room) {
        room.release();
        roomRepository.save(room);
    }
}
