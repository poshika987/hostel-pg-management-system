package com.hostel.management_system.service.observer;

import com.hostel.management_system.model.Complaint;
import com.hostel.management_system.model.ComplaintStatus;

public interface ComplaintStatusObserver {
    void onStatusChanged(Complaint complaint, ComplaintStatus oldStatus, String note);
}
