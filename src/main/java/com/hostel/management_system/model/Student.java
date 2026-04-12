package com.hostel.management_system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "id")
public class Student extends User {

    @Column(unique = true, nullable = false)
    private String studentCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentStatus status;

    // ========================
    // GETTERS AND SETTERS
    // ========================

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public StudentStatus getStatus() {
        return status;
    }

    public void setStatus(StudentStatus status) {
        this.status = status;
    }
}