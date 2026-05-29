package com.fooddelivery.paymentservice.controller;

import com.fooddelivery.paymentservice.dto.*;
import com.fooddelivery.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // process payment
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody @Valid PaymentRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.processPayment(request));
    }

    // get payment by id
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(
                paymentService.getPaymentById(paymentId));
    }

    // get payment by order id
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getByOrder(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(
                paymentService.getPaymentByOrderId(orderId));
    }

    // get all payments by customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PaymentResponse>> getByCustomer(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(
                paymentService.getPaymentsByCustomerId(customerId));
    }

    // refund payment
    @PostMapping("/refund/{orderId}")
    public ResponseEntity<RefundResponse> refund(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(
                paymentService.refundPayment(orderId));
    }
}