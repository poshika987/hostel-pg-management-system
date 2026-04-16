package com.hostel.management_system.service.report;

import com.hostel.management_system.model.Allocation;
import com.hostel.management_system.model.Room;
import com.hostel.management_system.repository.AllocationRepository;
import com.hostel.management_system.repository.RoomRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class OccupancyReport extends AbstractReport<Room> {

    private final RoomRepository roomRepository;
    private final AllocationRepository allocationRepository;
    private long allocationsInRange;

    public OccupancyReport(RoomRepository roomRepository, AllocationRepository allocationRepository) {
        this.roomRepository = roomRepository;
        this.allocationRepository = allocationRepository;
    }

    @Override
    public boolean supports(String reportType) {
        return "occupancy".equals(reportType);
    }

    @Override
    protected String title() {
        return "Occupancy Report";
    }

    @Override
    protected List<String> headers() {
        return List.of("Room", "Type", "Capacity", "Occupied", "Available", "Status", "Monthly Rent");
    }

    @Override
    protected List<Room> retrieveData(LocalDate startDate, LocalDate endDate) {
        allocationsInRange = allocationRepository.findAll().stream()
                .map(Allocation::getAllocatedAt)
                .filter(allocatedAt -> inRange(allocatedAt, startDate, endDate))
                .count();
        return roomRepository.findAll().stream()
                .sorted(Comparator.comparing(Room::getRoomId))
                .toList();
    }

    @Override
    protected List<List<String>> rows(List<Room> records) {
        return records.stream()
                .map(room -> List.of(
                        String.valueOf(room.getRoomId()),
                        room.getType(),
                        String.valueOf(room.getCapacity()),
                        String.valueOf(room.getOccupiedCount()),
                        String.valueOf(room.getAvailableSlots()),
                        room.getStatus().name(),
                        money(room.getPrice())
                ))
                .toList();
    }

    @Override
    protected Map<String, String> summary(List<Room> records) {
        int capacity = records.stream().mapToInt(Room::getCapacity).sum();
        int occupied = records.stream().mapToInt(Room::getOccupiedCount).sum();
        String rate = capacity == 0 ? "0%" : String.format(Locale.US, "%.1f%%", occupied * 100.0 / capacity);
        return orderedSummary(
                "Rooms", String.valueOf(records.size()),
                "Capacity", String.valueOf(capacity),
                "Occupied", String.valueOf(occupied),
                "Occupancy Rate", rate,
                "Allocations In Range", String.valueOf(allocationsInRange)
        );
    }
}
