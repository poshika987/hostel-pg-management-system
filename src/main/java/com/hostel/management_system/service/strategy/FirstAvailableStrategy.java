package com.hostel.management_system.service.strategy;

import com.hostel.management_system.model.Room;

import java.util.List;

public class FirstAvailableStrategy implements AllocationStrategy {

    @Override
    public Room allocate(List<Room> rooms) {
        return rooms.stream()
                .filter(Room::isAvailable)
                .findFirst()
                .orElse(null);
    }
}