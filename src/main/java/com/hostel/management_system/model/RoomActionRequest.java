package com.hostel.management_system.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_action_requests")
public class RoomActionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "current_room_id")
    private Room currentRoom;

    @ManyToOne
    @JoinColumn(name = "requested_room_id")
    private Room requestedRoom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomActionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime requestDate = LocalDateTime.now();

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public Room getRequestedRoom() {
        return requestedRoom;
    }

    public void setRequestedRoom(Room requestedRoom) {
        this.requestedRoom = requestedRoom;
    }

    public RoomActionType getType() {
        return type;
    }

    public void setType(RoomActionType type) {
        this.type = type;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }
}
