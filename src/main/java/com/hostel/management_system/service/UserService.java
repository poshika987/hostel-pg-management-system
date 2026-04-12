package com.hostel.management_system.service;

import com.hostel.management_system.model.*;
import com.hostel.management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public void registerStudent(Student student) {
        if (userRepository.findByEmail(student.getEmail()).isPresent()) {
            throw new RuntimeException("An account with this email already exists.");
        }
        student.setRole(Role.STUDENT);
        student.setStatus(StudentStatus.REGISTERED);
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        userRepository.save(student);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
