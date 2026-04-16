package com.hostel.management_system.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long invoiceId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "policy_id")
    private Integer policyId;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InvoiceStatus status = InvoiceStatus.PENDING;

    public Long getInvoiceId()                  { return invoiceId; }
    public void setInvoiceId(Long invoiceId)    { this.invoiceId = invoiceId; }

    public Long getStudentId()                  { return studentId; }
    public void setStudentId(Long studentId)    { this.studentId = studentId; }

    public Integer getPolicyId()                { return policyId; }
    public void setPolicyId(Integer policyId)   { this.policyId = policyId; }

    public double getAmount()                   { return amount; }
    public void setAmount(double amount)        { this.amount = amount; }

    public LocalDate getDueDate()               { return dueDate; }
    public void setDueDate(LocalDate dueDate)   { this.dueDate = dueDate; }

    public InvoiceStatus getStatus()            { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
}
