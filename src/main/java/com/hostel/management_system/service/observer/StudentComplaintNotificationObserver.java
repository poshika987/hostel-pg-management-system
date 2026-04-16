package com.hostel.management_system.service.observer;

import com.hostel.management_system.model.Complaint;
import com.hostel.management_system.model.ComplaintStatus;
import com.hostel.management_system.service.INotificationService;
import org.springframework.stereotype.Component;

@Component
public class StudentComplaintNotificationObserver implements ComplaintStatusObserver {

    private final INotificationService notificationService;

    public StudentComplaintNotificationObserver(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onStatusChanged(Complaint complaint, ComplaintStatus oldStatus, String note) {
        String message = "Complaint #" + complaint.getComplaintId()
                + " moved from " + oldStatus
                + " to " + complaint.getStatus()
                + (note == null || note.isBlank() ? "." : ": " + note);
        notificationService.notify(complaint.getStudent(), message);
    }
}
