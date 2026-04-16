package com.hostel.management_system.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "invoice_id")
    private Long invoiceId;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "payment_mode")
    private String paymentMode;

    @Column(name = "payment_channel")
    private String paymentChannel;

    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    public Long getPaymentId()                      { return paymentId; }
    public void setPaymentId(Long paymentId)        { this.paymentId = paymentId; }

    public Long getStudentId()                      { return studentId; }
    public void setStudentId(Long studentId)        { this.studentId = studentId; }

    public Long getInvoiceId()                      { return invoiceId; }
    public void setInvoiceId(Long invoiceId)        { this.invoiceId = invoiceId; }

    public double getAmount()                       { return amount; }
    public void setAmount(double amount)            { this.amount = amount; }

    public String getPaymentMode()                  { return paymentMode; }
    public void setPaymentMode(String paymentMode)  { this.paymentMode = paymentMode; }

    public String getPaymentChannel()                   { return paymentChannel; }
    public void setPaymentChannel(String paymentChannel) { this.paymentChannel = paymentChannel; }

    public String getReferenceId()                  { return referenceId; }
    public void setReferenceId(String referenceId)  { this.referenceId = referenceId; }

    public LocalDateTime getPaymentDate()           { return paymentDate; }
    public void setPaymentDate(LocalDateTime d)     { this.paymentDate = d; }

    public PaymentStatus getStatus()                { return status; }
    public void setStatus(PaymentStatus status)     { this.status = status; }
}
