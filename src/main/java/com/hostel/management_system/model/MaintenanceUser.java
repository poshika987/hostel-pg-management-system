package com.hostel.management_system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "maintenance_users")
@PrimaryKeyJoinColumn(name = "id")
public class MaintenanceUser extends User {

    @OneToOne(optional = false)
    @JoinColumn(name = "staff_id", unique = true)
    private MaintenanceStaff staff;

    public MaintenanceStaff getStaff() {
        return staff;
    }

    public void setStaff(MaintenanceStaff staff) {
        this.staff = staff;
    }
}
