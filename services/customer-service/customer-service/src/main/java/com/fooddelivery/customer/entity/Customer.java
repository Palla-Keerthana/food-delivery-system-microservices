package com.fooddelivery.customer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;
}