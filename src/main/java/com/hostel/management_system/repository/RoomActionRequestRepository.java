package com.hostel.management_system.repository;

import com.hostel.management_system.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomActionRequestRepository extends JpaRepository<RoomActionRequest, Long> {
    boolean existsByStudentAndStatus(Student student, BookingStatus status);
    List<RoomActionRequest> findByStatusOrderByRequestDateDesc(BookingStatus status);
    List<RoomActionRequest> findByStudentOrderByRequestDateDesc(Student student);
    List<RoomActionRequest> findAllByOrderByRequestDateDesc();
}
