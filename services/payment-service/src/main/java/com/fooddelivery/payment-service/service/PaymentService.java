package com.fooddelivery.payment_service.service;

import com.fooddelivery.payment_service.dto.*;
import java.util.List;

public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest request);

    PaymentResponse getPaymentById(Long paymentId);

    PaymentResponse getPaymentByOrderId(Long orderId);

    List<PaymentResponse> getPaymentsByCustomerId(
            Long customerId);

    RefundResponse refundPayment(Long orderId);
}