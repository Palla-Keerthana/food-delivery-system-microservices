package com.fooddelivery.paymentservice.service;

import com.fooddelivery.paymentservice.client.OrderResponse;
import com.fooddelivery.paymentservice.client.OrderServiceClient;
import com.fooddelivery.paymentservice.dto.*;
import com.fooddelivery.paymentservice.exception.*;
import com.fooddelivery.paymentservice.model.*;
import com.fooddelivery.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderServiceClient orderServiceClient;

    @Override
    public PaymentResponse processPayment(
            PaymentRequest request) {

        // Step 1 — validate order exists
        OrderResponse order;
        try {
            order = orderServiceClient
                    .getOrder(request.getOrderId());
        } catch (Exception e) {
            throw new RuntimeException(
                    "Order not found: "
                            + request.getOrderId());
        }

        // Step 2 — validate customer matches order
        if (!order.getCustomerId()
                .equals(request.getCustomerId())) {
            throw new RuntimeException(
                    "Customer does not match order");
        }

        // Step 3 — validate order status is PLACED
        if (!order.getStatus().equals("PLACED")) {
            throw new RuntimeException(
                    "Order is not in PLACED status. "
                            + "Current status: "
                            + order.getStatus());
        }

        // Step 4 — check duplicate payment
        if (paymentRepository.existsByOrderIdAndStatus(
                request.getOrderId(),
                PaymentStatus.SUCCESS)) {
            throw new PaymentAlreadyDoneException(
                    "Payment already done for order: "
                            + request.getOrderId());
        }

        // Step 5 — fetch amount from order-service
        // customer cannot manipulate amount
        Double amount = order.getTotalAmount();

        // Step 6 — build payment entity
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .customerId(request.getCustomerId())
                .amount(amount)
                .paymentMethod(request.getPaymentMethod())
                .build();

        // Step 7 — set method specific fields
        switch (request.getPaymentMethod()) {
            case UPI -> {
                payment.setUpiId(request.getUpiId());
                payment.setTransactionId(
                        generateTransactionId("UPI"));
            }
            case CREDIT_CARD, DEBIT_CARD -> {
                String cardNum = request.getCardNumber();
                payment.setCardLastFourDigits(
                        cardNum != null && cardNum.length() >= 4
                                ? cardNum.substring(cardNum.length() - 4)
                                : "0000");
                payment.setTransactionId(
                        generateTransactionId("CARD"));
            }
            case WALLET -> {
                payment.setWalletType(
                        request.getWalletType());
                payment.setTransactionId(
                        generateTransactionId("WAL"));
            }
        }

        // Step 8 — simulate payment
        boolean paymentSuccess = simulatePayment(request);

        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.SUCCESS);

            // Step 9 — notify order-service → CONFIRMED
            try {
                orderServiceClient.updateOrderStatus(
                        request.getOrderId(),
                        Map.of("status", "CONFIRMED"));
            } catch (Exception e) {
                System.out.println(
                        "Could not update order status: "
                                + e.getMessage());
            }

        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(
                    "Payment processing failed");

            // notify order-service → PAYMENT_FAILED
            try {
                orderServiceClient.updateOrderStatus(
                        request.getOrderId(),
                        Map.of("status", "PAYMENT_FAILED"));
            } catch (Exception e) {
                System.out.println(
                        "Could not update order status: "
                                + e.getMessage());
            }
        }

        return mapToResponse(
                paymentRepository.save(payment));
    }

    @Override
    public PaymentResponse getPaymentById(Long paymentId) {
        return mapToResponse(findPaymentById(paymentId));
    }

    @Override
    public PaymentResponse getPaymentByOrderId(
            Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found for order: " + orderId));
    }

    @Override
    public List<PaymentResponse> getPaymentsByCustomerId(
            Long customerId) {
        return paymentRepository
                .findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RefundResponse refundPayment(Long orderId) {

        Payment payment = paymentRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found for order: " + orderId));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new RuntimeException(
                    "Only successful payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundTime(LocalDateTime.now());
        paymentRepository.save(payment);

        // notify order-service → CANCELLED
        try {
            orderServiceClient.updateOrderStatus(
                    orderId,
                    Map.of("status", "CANCELLED"));
        } catch (Exception e) {
            System.out.println(
                    "Could not update order status: "
                            + e.getMessage());
        }

        return RefundResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(orderId)
                .refundAmount(payment.getAmount())
                .status("REFUNDED")
                .refundTime(payment.getRefundTime())
                .build();
    }

    // ── helpers ──────────────────────────────────────

    private boolean simulatePayment(PaymentRequest req) {
        return true; // always success for testing
    }

    private String generateTransactionId(String prefix) {
        return prefix + "_"
                + System.currentTimeMillis()
                + "_"
                + (int)(Math.random() * 10000);
    }

    private Payment findPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found: " + paymentId));
    }

    private PaymentResponse mapToResponse(Payment p) {
        return PaymentResponse.builder()
                .paymentId(p.getPaymentId())
                .orderId(p.getOrderId())
                .customerId(p.getCustomerId())
                .amount(p.getAmount())
                .paymentMethod(p.getPaymentMethod())
                .status(p.getStatus())
                .transactionId(p.getTransactionId())
                .failureReason(p.getFailureReason())
                .paymentTime(p.getPaymentTime())
                .build();
    }
}