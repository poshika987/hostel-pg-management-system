package com.hostel.management_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "maintenance_staff")
public class MaintenanceStaff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staffId;

    @Column(nullable = false)
    @NotBlank(message = "Staff name is required.")
    @Size(max = 80, message = "Staff name must be 80 characters or less.")
    private String name;

    @NotBlank(message = "Staff role is required.")
    @Size(max = 80, message = "Staff role must be 80 characters or less.")
    private String role;

    @NotBlank(message = "Contact information is required.")
    @Size(max = 40, message = "Contact information must be 40 characters or less.")
    private String contact;

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
