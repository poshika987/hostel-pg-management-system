package com.hostel.management_system.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complaintId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "assigned_staff_id")
    private MaintenanceStaff assignedStaff;

    @Column(nullable = false, length = 80)
    private String title;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus status = ComplaintStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintPriority priority = ComplaintPriority.MEDIUM;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("photoId ASC")
    private List<ComplaintPhoto> photos = new ArrayList<>();

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("updatedAt ASC")
    private List<ComplaintStatusUpdate> timeline = new ArrayList<>();

    public Complaint() {
    }

    private Complaint(Builder builder) {
        this.student = builder.student;
        this.room = builder.room;
        this.title = builder.title;
        this.category = builder.category;
        this.description = builder.description;
        this.priority = builder.priority;
        this.status = ComplaintStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public static Builder builder(Student student, String title, String category, String description) {
        return new Builder(student, title, category, description);
    }

    public void assignTo(MaintenanceStaff staff) {
        this.assignedStaff = staff;
        changeStatus(ComplaintStatus.ASSIGNED);
    }

    public void changeStatus(ComplaintStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public void addPhoto(String filePath, String originalFilename) {
        ComplaintPhoto photo = new ComplaintPhoto();
        photo.setComplaint(this);
        photo.setFilePath(filePath);
        photo.setOriginalFilename(originalFilename);
        this.photos.add(photo);
    }

    public void addTimelineEntry(ComplaintStatus status, String note) {
        ComplaintStatusUpdate update = new ComplaintStatusUpdate();
        update.setComplaint(this);
        update.setStatus(status);
        update.setNote(note);
        update.setUpdatedAt(LocalDateTime.now());
        this.timeline.add(update);
    }

    public static final class Builder {
        private final Student student;
        private final String title;
        private final String category;
        private final String description;
        private Room room;
        private ComplaintPriority priority = ComplaintPriority.MEDIUM;

        private Builder(Student student, String title, String category, String description) {
            this.student = student;
            this.title = title;
            this.category = category;
            this.description = description;
        }

        public Builder room(Room room) {
            this.room = room;
            return this;
        }

        public Builder priority(ComplaintPriority priority) {
            if (priority != null) {
                this.priority = priority;
            }
            return this;
        }

        public Complaint build() {
            return new Complaint(this);
        }
    }

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public MaintenanceStaff getAssignedStaff() {
        return assignedStaff;
    }

    public void setAssignedStaff(MaintenanceStaff assignedStaff) {
        this.assignedStaff = assignedStaff;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public ComplaintPriority getPriority() {
        return priority;
    }

    public void setPriority(ComplaintPriority priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ComplaintPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<ComplaintPhoto> photos) {
        this.photos = photos;
    }

    public List<ComplaintStatusUpdate> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<ComplaintStatusUpdate> timeline) {
        this.timeline = timeline;
    }
}
