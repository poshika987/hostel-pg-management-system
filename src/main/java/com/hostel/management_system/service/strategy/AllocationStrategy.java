package com.hostel.management_system.service.strategy;

import com.hostel.management_system.model.Room;

import java.util.List;

public interface AllocationStrategy {
    Room allocate(List<Room> rooms);
}