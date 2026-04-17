package com.hostel.management_system.repository;

import com.hostel.management_system.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentDisputeRepository extends JpaRepository<PaymentDispute, Long> {
    List<PaymentDispute> findByStatusOrderByCreatedAtDesc(PaymentDisputeStatus status);
    List<PaymentDispute> findByStudentOrderByCreatedAtDesc(Student student);
}
