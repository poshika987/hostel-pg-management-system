package com.hostel.management_system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "accountants")
public class Accountant extends User {
}