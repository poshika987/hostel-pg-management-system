package com.hostel.management_system.repository;

import com.hostel.management_system.model.RentPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentPolicyRepository extends JpaRepository<RentPolicy, Integer> {
}
