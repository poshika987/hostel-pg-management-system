package com.hostel.management_system.service.report;

import com.hostel.management_system.model.Complaint;
import com.hostel.management_system.model.ComplaintStatus;
import com.hostel.management_system.repository.ComplaintRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class ComplaintReport extends AbstractReport<Complaint> {

    private final ComplaintRepository complaintRepository;

    public ComplaintReport(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    @Override
    public boolean supports(String reportType) {
        return "complaint".equals(reportType);
    }

    @Override
    protected String title() {
        return "Complaint Report";
    }

    @Override
    protected List<String> headers() {
        return List.of("Complaint ID", "Student", "Room", "Category", "Title", "Priority", "Status", "Assigned Staff", "Created At");
    }

    @Override
    protected List<Complaint> retrieveData(LocalDate startDate, LocalDate endDate) {
        return complaintRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(complaint -> inRange(complaint.getCreatedAt(), startDate, endDate))
                .toList();
    }

    @Override
    protected List<List<String>> rows(List<Complaint> records) {
        return records.stream()
                .map(complaint -> List.of(
                        String.valueOf(complaint.getComplaintId()),
                        complaint.getStudent().getName(),
                        complaint.getRoom() == null ? "Unassigned" : "Room " + complaint.getRoom().getRoomId(),
                        complaint.getCategory(),
                        complaint.getTitle(),
                        complaint.getPriority().name(),
                        complaint.getStatus().name(),
                        complaint.getAssignedStaff() == null ? "Not assigned" : complaint.getAssignedStaff().getName(),
                        format(complaint.getCreatedAt())
                ))
                .toList();
    }

    @Override
    protected Map<String, String> summary(List<Complaint> records) {
        return orderedSummary(
                "Records", String.valueOf(records.size()),
                "Open", String.valueOf(records.stream().filter(c -> c.getStatus() != ComplaintStatus.CLOSED).count()),
                "Closed", String.valueOf(records.stream().filter(c -> c.getStatus() == ComplaintStatus.CLOSED).count())
        );
    }
}
