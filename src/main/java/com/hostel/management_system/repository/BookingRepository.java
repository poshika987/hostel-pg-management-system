package com.hostel.management_system.repository;

import com.hostel.management_system.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByStudent(Student student);
    boolean existsByStudentAndStatus(Student student, BookingStatus status);
}
