package com.hostel.management_system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "wardens")
public class Warden extends User {
}