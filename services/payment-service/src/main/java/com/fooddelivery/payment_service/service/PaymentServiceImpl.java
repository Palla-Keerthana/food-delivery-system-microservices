package com.fooddelivery.payment_service.service;

import com.fooddelivery.payment_service.client.OrderServiceClient;
import com.fooddelivery.payment_service.dto.*;
import com.fooddelivery.payment_service.exception.*;
import com.fooddelivery.payment_service.model.*;
import com.fooddelivery.payment_service.repository.PaymentRepository;
import com.fooddelivery.payment_service.dto.RefundResponse;
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

        // check if payment already done for this order
        if (paymentRepository.existsByOrderIdAndStatus(
                request.getOrderId(),
                PaymentStatus.SUCCESS)) {
            throw new PaymentAlreadyDoneException(
                    "Payment already done for order: "
                            + request.getOrderId());
        }

        // use amount from request directly for now
        // TODO: fetch from order-service when integrated
        Double amount = request.getAmount();

        // build payment entity
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .customerId(request.getCustomerId())
                .amount(amount)
                .paymentMethod(request.getPaymentMethod())
                .build();

        // set method specific fields
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
                                ? cardNum.substring(
                                cardNum.length() - 4)
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

        // simulate payment processing
        boolean paymentSuccess = simulatePayment(request);

        if (paymentSuccess) {
            // set SUCCESS before save
            payment.setStatus(PaymentStatus.SUCCESS);

            // notify order-service — skip if not running
            try {
                Map<String, String> statusUpdate =
                        new HashMap<>();
                statusUpdate.put("status", "PAYMENT_SUCCESS");
                orderServiceClient.updateOrderStatus(
                        request.getOrderId(), statusUpdate);
            } catch (Exception e) {
                System.out.println(
                        "Order service not available: "
                                + e.getMessage());
            }
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(
                    "Payment processing failed");

            // notify order-service — skip if not running
            try {
                Map<String, String> statusUpdate =
                        new HashMap<>();
                statusUpdate.put("status", "PAYMENT_FAILED");
                orderServiceClient.updateOrderStatus(
                        request.getOrderId(), statusUpdate);
            } catch (Exception e) {
                System.out.println(
                        "Order service not available: "
                                + e.getMessage());
            }
        }

        return mapToResponse(paymentRepository.save(payment));
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
                        "Payment not found for order: "
                                + orderId));
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
                        "Payment not found for order: "
                                + orderId));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new RuntimeException(
                    "Only successful payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundTime(LocalDateTime.now());
        paymentRepository.save(payment);

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