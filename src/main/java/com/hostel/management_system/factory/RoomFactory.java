package com.hostel.management_system.factory;

import com.hostel.management_system.model.Room;
import com.hostel.management_system.model.RoomStatus;

/**
 * Creational Pattern – Factory Method:
 * Centralises Room instantiation logic so that callers (e.g. DataInitializer)
 * do not need to know how to configure each room type.
 *
 * Adding a new room type (e.g. "SUITE") requires changing only this class,
 * not every place that creates rooms (SOLID – Open/Closed Principle).
 *
 * Supported types:
 *   SINGLE    – 1 occupant, ₹8,000/month
 *   DOUBLE    – 2 occupants, ₹6,000/month per head
 *   TRIPLE    – 3 occupants, ₹4,500/month per head
 *   DORMITORY – 6 occupants, ₹3,000/month per head
 */
public class RoomFactory {

    private RoomFactory() {}   // static-utility class — prevent instantiation

    public static Room createRoom(Integer roomId, String type) {
        Room room = new Room();
        room.setRoomId(roomId);
        room.setType(type.toUpperCase());
        room.setStatus(RoomStatus.AVAILABLE);
        room.setOccupiedCount(0);

        switch (type.toUpperCase()) {
            case "SINGLE"    -> { room.setCapacity(1); room.setPrice(8000); }
            case "DOUBLE"    -> { room.setCapacity(2); room.setPrice(6000); }
            case "TRIPLE"    -> { room.setCapacity(3); room.setPrice(4500); }
            case "DORMITORY" -> { room.setCapacity(6); room.setPrice(3000); }
            default -> throw new IllegalArgumentException("Unknown room type: " + type);
        }

        return room;
    }
}
