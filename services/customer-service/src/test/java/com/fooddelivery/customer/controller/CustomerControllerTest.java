package com.fooddelivery.customer.controller;

import com.fooddelivery.customer.dto.CustomerRequest;
import com.fooddelivery.customer.dto.CustomerResponse;
import com.fooddelivery.customer.exception.GlobalExceptionHandler;
import com.fooddelivery.customer.exception.InvalidRequestException;
import com.fooddelivery.customer.exception.ResourceNotFoundException;
import com.fooddelivery.customer.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock CustomerService customerService;
    @InjectMocks CustomerController customerController;

    MockMvc mvc;
    ObjectMapper mapper;
    CustomerResponse customerResponse;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        customerResponse = new CustomerResponse(
                1L, 1L, "John Kumar", "9876543210", "Chennai, TN");
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private CustomerRequest buildRequest(Long userId, String name,
                                         String phone, String address) {
        CustomerRequest req = new CustomerRequest();
        req.setUserId(userId);
        req.setCustomerName(name);
        req.setPhone(phone);
        req.setAddress(address);
        return req;
    }

    // ─── createCustomer ───────────────────────────────────────────────────────

    @Test
    void createCustomer_validRequest_returns201() throws Exception {
        when(customerService.createCustomer(any(CustomerRequest.class)))
                .thenReturn(customerResponse);

        mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                buildRequest(1L, "John Kumar",
                                        "9876543210", "Chennai, TN"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.customerName").value("John Kumar"))
                .andExpect(jsonPath("$.phone").value("9876543210"))
                .andExpect(jsonPath("$.address").value("Chennai, TN"));
    }

    @Test
    void createCustomer_missingUserId_returns400() throws Exception {
        mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                buildRequest(null, "John",
                                        "9876543210", "Chennai"))))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).createCustomer(any());
    }

    @Test
    void createCustomer_missingName_returns400() throws Exception {
        mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                buildRequest(1L, null,
                                        "9876543210", "Chennai"))))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).createCustomer(any());
    }

    @Test
    void createCustomer_alreadyExists_returns400() throws Exception {
        when(customerService.createCustomer(any(CustomerRequest.class)))
                .thenThrow(new InvalidRequestException(
                        "Customer profile already exists"));

        mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                buildRequest(1L, "John",
                                        "9876543210", "Chennai"))))
                .andExpect(status().isBadRequest());
    }

    // ─── getCustomerById ──────────────────────────────────────────────────────

    @Test
    void getCustomerById_found_returns200() throws Exception {
        when(customerService.getCustomerById(1L)).thenReturn(customerResponse);

        mvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.customerName").value("John Kumar"))
                .andExpect(jsonPath("$.phone").value("9876543210"))
                .andExpect(jsonPath("$.address").value("Chennai, TN"));
    }

    @Test
    void getCustomerById_notFound_returns404() throws Exception {
        when(customerService.getCustomerById(999L))
                .thenThrow(new ResourceNotFoundException("Customer not found"));

        mvc.perform(get("/api/customers/999"))
                .andExpect(status().isNotFound());
    }

    // ─── getCustomerByUserId ──────────────────────────────────────────────────

    @Test
    void getCustomerByUserId_found_returns200() throws Exception {
        when(customerService.getCustomerByUserId(1L)).thenReturn(customerResponse);

        mvc.perform(get("/api/customers/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.customerName").value("John Kumar"));
    }

    @Test
    void getCustomerByUserId_notFound_returns404() throws Exception {
        when(customerService.getCustomerByUserId(999L))
                .thenThrow(new ResourceNotFoundException(
                        "Customer not found for user"));

        mvc.perform(get("/api/customers/user/999"))
                .andExpect(status().isNotFound());
    }

    // ─── updateCustomer ───────────────────────────────────────────────────────

    @Test
    void updateCustomer_validRequest_returns200() throws Exception {
        CustomerResponse updated = new CustomerResponse(
                1L, 1L, "John Updated", "9999999999", "Bangalore");
        when(customerService.updateCustomer(eq(1L), any(CustomerRequest.class)))
                .thenReturn(updated);

        mvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                buildRequest(1L, "John Updated",
                                        "9999999999", "Bangalore"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("John Updated"))
                .andExpect(jsonPath("$.phone").value("9999999999"))
                .andExpect(jsonPath("$.address").value("Bangalore"));
    }

    @Test
    void updateCustomer_notFound_returns404() throws Exception {
        when(customerService.updateCustomer(eq(999L), any(CustomerRequest.class)))
                .thenThrow(new ResourceNotFoundException("Customer not found"));

        mvc.perform(put("/api/customers/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                buildRequest(1L, "John",
                                        "9876543210", "Chennai"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCustomer_missingName_returns400() throws Exception {
        mvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                buildRequest(1L, null,
                                        "9876543210", "Chennai"))))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).updateCustomer(any(), any());
    }

    @Test
    void updateCustomer_missingUserId_returns400() throws Exception {
        mvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                buildRequest(null, "John",
                                        "9876543210", "Chennai"))))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).updateCustomer(any(), any());
    }
}