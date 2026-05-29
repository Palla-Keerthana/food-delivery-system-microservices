package com.fooddelivery.paymentservice.service;

import com.fooddelivery.paymentservice.dto.*;
import java.util.List;

public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest request);

    PaymentResponse getPaymentById(Long paymentId);

    PaymentResponse getPaymentByOrderId(Long orderId);

    List<PaymentResponse> getPaymentsByCustomerId(
            Long customerId);

    RefundResponse refundPayment(Long orderId);
}