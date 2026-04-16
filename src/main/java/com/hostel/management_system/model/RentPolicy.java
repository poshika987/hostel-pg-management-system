package com.hostel.management_system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rent_policies")
public class RentPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Integer policyId;

    @Column(name = "monthly_rent", nullable = false)
    private double monthlyRent;

    @Column(name = "late_fee")
    private double lateFee;

    public Integer getPolicyId()                { return policyId; }
    public void setPolicyId(Integer policyId)   { this.policyId = policyId; }

    public double getMonthlyRent()              { return monthlyRent; }
    public void setMonthlyRent(double r)        { this.monthlyRent = r; }

    public double getLateFee()                  { return lateFee; }
    public void setLateFee(double f)            { this.lateFee = f; }
}
