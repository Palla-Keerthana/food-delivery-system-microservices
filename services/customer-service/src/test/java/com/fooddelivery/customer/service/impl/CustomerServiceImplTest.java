package com.fooddelivery.customer.service.impl;

import com.fooddelivery.customer.dto.CustomerRequest;
import com.fooddelivery.customer.dto.CustomerResponse;
import com.fooddelivery.customer.entity.Customer;
import com.fooddelivery.customer.exception.InvalidRequestException;
import com.fooddelivery.customer.exception.ResourceNotFoundException;
import com.fooddelivery.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock CustomerRepository customerRepository;
    @InjectMocks CustomerServiceImpl customerService;

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Customer buildCustomer(Long customerId, Long userId,
                                   String name, String phone, String address) {
        Customer c = new Customer();
        c.setCustomerId(customerId);
        c.setUserId(userId);
        c.setCustomerName(name);
        c.setPhone(phone);
        c.setAddress(address);
        return c;
    }

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
    void createCustomer_success_returnsResponse() {
        when(customerRepository.existsByUserId(1L)).thenReturn(false);
        Customer saved = buildCustomer(1L, 1L, "John", "9876543210", "Chennai");
        when(customerRepository.save(any(Customer.class))).thenReturn(saved);

        CustomerResponse result = customerService.createCustomer(
                buildRequest(1L, "John", "9876543210", "Chennai"));

        assertThat(result.getCustomerId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getCustomerName()).isEqualTo("John");
        assertThat(result.getPhone()).isEqualTo("9876543210");
        assertThat(result.getAddress()).isEqualTo("Chennai");
    }

    @Test
    void createCustomer_alreadyExists_throwsInvalidRequestException() {
        when(customerRepository.existsByUserId(1L)).thenReturn(true);

        assertThatThrownBy(() -> customerService.createCustomer(
                buildRequest(1L, "John", "9876543210", "Chennai")))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Customer profile already exists");

        verify(customerRepository, never()).save(any());
    }

    @Test
    void createCustomer_savesCorrectFields() {
        when(customerRepository.existsByUserId(1L)).thenReturn(false);
        Customer saved = buildCustomer(1L, 1L, "John", "9876543210", "Chennai");
        when(customerRepository.save(any())).thenReturn(saved);

        customerService.createCustomer(
                buildRequest(1L, "John", "9876543210", "Chennai"));

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(1L);
        assertThat(captor.getValue().getCustomerName()).isEqualTo("John");
        assertThat(captor.getValue().getPhone()).isEqualTo("9876543210");
        assertThat(captor.getValue().getAddress()).isEqualTo("Chennai");
    }

    // ─── getCustomerById ──────────────────────────────────────────────────────

    @Test
    void getCustomerById_found_returnsResponse() {
        Customer c = buildCustomer(1L, 1L, "John", "9876543210", "Chennai");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));

        CustomerResponse result = customerService.getCustomerById(1L);

        assertThat(result.getCustomerId()).isEqualTo(1L);
        assertThat(result.getCustomerName()).isEqualTo("John");
    }

    @Test
    void getCustomerById_notFound_throwsResourceNotFoundException() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found");
    }

    @Test
    void getCustomerById_mapsAllFieldsCorrectly() {
        Customer c = buildCustomer(1L, 1L, "John", "9876543210", "Chennai");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));

        CustomerResponse result = customerService.getCustomerById(1L);

        assertThat(result.getCustomerId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getCustomerName()).isEqualTo("John");
        assertThat(result.getPhone()).isEqualTo("9876543210");
        assertThat(result.getAddress()).isEqualTo("Chennai");
    }

    // ─── getCustomerByUserId ──────────────────────────────────────────────────

    @Test
    void getCustomerByUserId_found_returnsResponse() {
        Customer c = buildCustomer(1L, 1L, "John", "9876543210", "Chennai");
        when(customerRepository.findByUserId(1L)).thenReturn(Optional.of(c));

        CustomerResponse result = customerService.getCustomerByUserId(1L);

        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getCustomerName()).isEqualTo("John");
    }

    @Test
    void getCustomerByUserId_notFound_throwsResourceNotFoundException() {
        when(customerRepository.findByUserId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerByUserId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found for user");
    }

    // ─── updateCustomer ───────────────────────────────────────────────────────

    @Test
    void updateCustomer_success_returnsUpdatedResponse() {
        Customer existing = buildCustomer(1L, 1L, "John", "9876543210", "Chennai");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        Customer updated = buildCustomer(1L, 1L, "John Updated",
                "9999999999", "Bangalore");
        when(customerRepository.save(any())).thenReturn(updated);

        CustomerResponse result = customerService.updateCustomer(1L,
                buildRequest(1L, "John Updated", "9999999999", "Bangalore"));

        assertThat(result.getCustomerName()).isEqualTo("John Updated");
        assertThat(result.getPhone()).isEqualTo("9999999999");
        assertThat(result.getAddress()).isEqualTo("Bangalore");
    }

    @Test
    void updateCustomer_notFound_throwsResourceNotFoundException() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.updateCustomer(999L,
                buildRequest(1L, "John", "9876543210", "Chennai")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found");

        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateCustomer_updatesCorrectFields() {
        Customer existing = buildCustomer(1L, 1L, "John", "9876543210", "Chennai");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        Customer updated = buildCustomer(1L, 1L, "John Updated",
                "9999999999", "Bangalore");
        when(customerRepository.save(any())).thenReturn(updated);

        customerService.updateCustomer(1L,
                buildRequest(1L, "John Updated", "9999999999", "Bangalore"));

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());
        assertThat(captor.getValue().getCustomerName()).isEqualTo("John Updated");
        assertThat(captor.getValue().getPhone()).isEqualTo("9999999999");
        assertThat(captor.getValue().getAddress()).isEqualTo("Bangalore");
    }

    @Test
    void updateCustomer_doesNotChangeUserId() {
        Customer existing = buildCustomer(1L, 1L, "John", "9876543210", "Chennai");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        Customer updated = buildCustomer(1L, 1L, "John Updated",
                "9999999999", "Bangalore");
        when(customerRepository.save(any())).thenReturn(updated);

        customerService.updateCustomer(1L,
                buildRequest(1L, "John Updated", "9999999999", "Bangalore"));

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(1L);
    }
}
