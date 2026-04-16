package com.hostel.management_system.repository;

import com.hostel.management_system.model.Invoice;
import com.hostel.management_system.model.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByStudentId(Long studentId);
    List<Invoice> findByStudentIdAndStatus(Long studentId, InvoiceStatus status);
}
