package com.hostel.management_system.repository;

import com.hostel.management_system.model.Room;
import com.hostel.management_system.model.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findByStatus(RoomStatus status);
}