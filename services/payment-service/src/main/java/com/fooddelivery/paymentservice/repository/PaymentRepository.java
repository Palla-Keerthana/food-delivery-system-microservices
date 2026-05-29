package com.fooddelivery.paymentservice.repository;

import com.fooddelivery.paymentservice.model.Payment;
import com.fooddelivery.paymentservice.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    List<Payment> findByCustomerId(Long customerId);

    List<Payment> findByStatus(PaymentStatus status);

    boolean existsByOrderIdAndStatus(
            Long orderId, PaymentStatus status);
}