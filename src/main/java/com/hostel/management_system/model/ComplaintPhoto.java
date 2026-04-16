package com.hostel.management_system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "complaint_photos")
public class ComplaintPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String originalFilename;

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
}
