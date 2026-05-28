package com.fooddelivery.customer_service.service;

import com.fooddelivery.customer_service.dto.CustomerRequest;
import com.fooddelivery.customer_service.dto.CustomerResponse;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse getCustomerById(Long customerId);

    CustomerResponse getCustomerByUserId(Long userId);

    CustomerResponse updateCustomer(Long customerId, CustomerRequest request);
}