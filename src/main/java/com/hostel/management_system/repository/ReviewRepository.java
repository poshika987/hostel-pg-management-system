package com.hostel.management_system.repository;

import com.hostel.management_system.model.Review;
import com.hostel.management_system.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByStudentOrderByCreatedAtDesc(Student student);
}
