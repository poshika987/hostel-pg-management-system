package com.hostel.management_system.repository;

import com.hostel.management_system.model.Payment;
import com.hostel.management_system.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByStudentIdOrderByPaymentDateDesc(Long studentId);
    List<Payment> findAllByStatusOrderByPaymentDateDesc(PaymentStatus status);
}
