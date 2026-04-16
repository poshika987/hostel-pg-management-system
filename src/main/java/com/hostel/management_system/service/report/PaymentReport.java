package com.hostel.management_system.service.report;

import com.hostel.management_system.model.Payment;
import com.hostel.management_system.model.PaymentStatus;
import com.hostel.management_system.repository.PaymentRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class PaymentReport extends AbstractReport<Payment> {

    private final PaymentRepository paymentRepository;

    public PaymentReport(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public boolean supports(String reportType) {
        return "payment".equals(reportType);
    }

    @Override
    protected String title() {
        return "Payment Report";
    }

    @Override
    protected List<String> headers() {
        return List.of("Payment ID", "Student ID", "Invoice ID", "Amount", "Mode", "Channel", "Status", "Paid At");
    }

    @Override
    protected List<Payment> retrieveData(LocalDate startDate, LocalDate endDate) {
        return paymentRepository.findAll().stream()
                .filter(payment -> inRange(payment.getPaymentDate(), startDate, endDate))
                .sorted(Comparator.comparing(Payment::getPaymentDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    @Override
    protected List<List<String>> rows(List<Payment> records) {
        return records.stream()
                .map(payment -> List.of(
                        String.valueOf(payment.getPaymentId()),
                        nullSafe(payment.getStudentId()),
                        nullSafe(payment.getInvoiceId()),
                        money(payment.getAmount()),
                        nullSafe(payment.getPaymentMode()),
                        nullSafe(payment.getPaymentChannel()),
                        nullSafe(payment.getStatus()),
                        format(payment.getPaymentDate())
                ))
                .toList();
    }

    @Override
    protected Map<String, String> summary(List<Payment> records) {
        double total = records.stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.SUCCESSFUL)
                .mapToDouble(Payment::getAmount)
                .sum();
        return orderedSummary(
                "Records", String.valueOf(records.size()),
                "Successful Amount", money(total),
                "Refund Requests", String.valueOf(records.stream().filter(p -> p.getStatus() == PaymentStatus.REFUND_REQUESTED).count())
        );
    }
}
