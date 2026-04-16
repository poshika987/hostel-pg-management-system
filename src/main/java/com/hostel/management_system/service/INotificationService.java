package com.hostel.management_system.service;

import com.hostel.management_system.model.User;

public interface INotificationService {
    void notify(User user, String message);
}
