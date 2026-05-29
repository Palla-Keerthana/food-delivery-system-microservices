package com.fooddelivery.customer.service;

import com.fooddelivery.customer.dto.CustomerRequest;
import com.fooddelivery.customer.dto.CustomerResponse;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse getCustomerById(Long customerId);

    CustomerResponse getCustomerByUserId(Long userId);

    CustomerResponse updateCustomer(Long customerId, CustomerRequest request);
}