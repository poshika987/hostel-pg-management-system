package com.hostel.management_system.config;

import com.hostel.management_system.factory.RoomFactory;
import com.hostel.management_system.model.*;
import com.hostel.management_system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds default admin/warden users and sample rooms using the Factory Method.
 * Creational Pattern – Factory Method: RoomFactory.createRoom() instantiates
 * correctly typed Room objects without exposing construction details here.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedRooms();
    }

    private void seedUsers() {
        if (userRepository.findByEmail("admin@hostel.com").isEmpty()) {
            Admin admin = new Admin();
            admin.setName("Admin"); admin.setEmail("admin@hostel.com");
            admin.setPhone("9999999999");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }
        if (userRepository.findByEmail("warden@hostel.com").isEmpty()) {
            Warden w = new Warden();
            w.setName("Warden"); w.setEmail("warden@hostel.com");
            w.setPhone("8888888888");
            w.setPassword(passwordEncoder.encode("warden123"));
            w.setRole(Role.WARDEN);
            userRepository.save(w);
        }
    }

    private void seedRooms() {
        if (roomRepository.count() == 0) {
            // Creational Pattern – Factory Method creates typed rooms with correct config
            roomRepository.save(RoomFactory.createRoom(101, "SINGLE"));
            roomRepository.save(RoomFactory.createRoom(102, "SINGLE"));
            roomRepository.save(RoomFactory.createRoom(103, "SINGLE"));
            roomRepository.save(RoomFactory.createRoom(201, "DOUBLE"));
            roomRepository.save(RoomFactory.createRoom(202, "DOUBLE"));
            roomRepository.save(RoomFactory.createRoom(203, "DOUBLE"));
            roomRepository.save(RoomFactory.createRoom(301, "TRIPLE"));
            roomRepository.save(RoomFactory.createRoom(302, "TRIPLE"));
            roomRepository.save(RoomFactory.createRoom(401, "DORMITORY"));
            roomRepository.save(RoomFactory.createRoom(402, "DORMITORY"));
        }
    }
}
