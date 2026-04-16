package com.hostel.management_system.config;

import com.hostel.management_system.factory.RoomFactory;
import com.hostel.management_system.model.*;
import com.hostel.management_system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Seeds default users, rooms, and rent policy on startup.
 * Creational Pattern – Factory Method: RoomFactory.createRoom() builds typed rooms.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private RentPolicyRepository rentPolicyRepository;
    @Autowired private MaintenanceStaffRepository maintenanceStaffRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        if (isMySql()) {
            fixAllocationIndexes();
            fixPaymentSchema();
        }
        seedUsers();
        seedRooms();
        seedMaintenanceStaff();
        syncRoomDefaults();
        seedRentPolicy();
    }

    private boolean isMySql() {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            return connection.getMetaData().getDatabaseProductName().toLowerCase().contains("mysql");
        } catch (SQLException ex) {
            return false;
        }
    }

    private void fixAllocationIndexes() {
        ensureIndexExists("idx_allocations_student_id",
                "CREATE INDEX idx_allocations_student_id ON allocations(student_id)");
        ensureIndexExists("idx_allocations_room_id",
                "CREATE INDEX idx_allocations_room_id ON allocations(room_id)");
        ensureIndexExists("idx_allocations_student_status",
                "CREATE INDEX idx_allocations_student_status ON allocations(student_id, status)");

        Integer indexCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.statistics
                WHERE table_schema = DATABASE()
                  AND table_name = 'allocations'
                  AND index_name = 'unique_active_allocation'
                """, Integer.class);

        if (indexCount != null && indexCount > 0) {
            jdbcTemplate.execute("ALTER TABLE allocations DROP INDEX unique_active_allocation");
        }
    }

    private void ensureIndexExists(String indexName, String ddl) {
        Integer indexCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.statistics
                WHERE table_schema = DATABASE()
                  AND table_name = 'allocations'
                  AND index_name = ?
                """, Integer.class, indexName);

        if (indexCount != null && indexCount == 0) {
            jdbcTemplate.execute(ddl);
        }
    }

    private void fixPaymentSchema() {
        ensureColumnExists("payments", "payment_mode",
                "ALTER TABLE payments ADD COLUMN payment_mode VARCHAR(30) NULL AFTER amount");
        ensureColumnExists("payments", "payment_channel",
                "ALTER TABLE payments ADD COLUMN payment_channel VARCHAR(30) NULL AFTER payment_mode");
        ensureColumnExists("payments", "reference_id",
                "ALTER TABLE payments ADD COLUMN reference_id VARCHAR(80) NULL AFTER payment_channel");

        String paymentStatusType = jdbcTemplate.queryForObject("""
                SELECT COLUMN_TYPE
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = 'payments'
                  AND column_name = 'status'
                """, String.class);

        if (paymentStatusType != null && !paymentStatusType.contains("REFUND_REQUESTED")) {
            jdbcTemplate.execute("""
                    ALTER TABLE payments
                    MODIFY COLUMN status
                    ENUM('SUCCESSFUL','REFUND_REQUESTED','FAILED','REFUNDED') NOT NULL
                    """);
        }
    }

    private void ensureColumnExists(String tableName, String columnName, String ddl) {
        Integer columnCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = ?
                  AND column_name = ?
                """, Integer.class, tableName, columnName);

        if (columnCount != null && columnCount == 0) {
            jdbcTemplate.execute(ddl);
        }
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

    private void seedMaintenanceStaff() {
        if (maintenanceStaffRepository.count() == 0) {
            maintenanceStaffRepository.save(createMaintenanceStaff("Ravi Kumar", "Electrician", "9000011111"));
            maintenanceStaffRepository.save(createMaintenanceStaff("Anita Rao", "Plumber", "9000022222"));
            maintenanceStaffRepository.save(createMaintenanceStaff("Kiran Shetty", "General Maintenance", "9000033333"));
        }
    }

    private MaintenanceStaff createMaintenanceStaff(String name, String role, String contact) {
        MaintenanceStaff staff = new MaintenanceStaff();
        staff.setName(name);
        staff.setRole(role);
        staff.setContact(contact);
        return staff;
    }

    private void syncRoomDefaults() {
        roomRepository.findAll().forEach(room -> {
            Room defaults = RoomFactory.createRoom(room.getRoomId(), room.getType());
            boolean changed = false;

            if (room.getCapacity() <= 0) {
                room.setCapacity(defaults.getCapacity());
                changed = true;
            }
            if (room.getPrice() <= 0) {
                room.setPrice(defaults.getPrice());
                changed = true;
            }
            if (changed) {
                roomRepository.save(room);
            }
        });
    }

    private void seedRentPolicy() {
        if (rentPolicyRepository.count() == 0) {
            RentPolicy policy = new RentPolicy();
            policy.setMonthlyRent(5000.0);
            policy.setLateFee(100.0);
            rentPolicyRepository.save(policy);
            return;
        }

        rentPolicyRepository.findAll().forEach(policy -> {
            if (policy.getLateFee() != 100.0) {
                policy.setLateFee(100.0);
                rentPolicyRepository.save(policy);
            }
        });
    }
}
