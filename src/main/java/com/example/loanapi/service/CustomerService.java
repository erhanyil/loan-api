package com.example.loanapi.service;


import com.example.loanapi.model.CustomerModel;
import com.example.loanapi.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<CustomerModel> getAllCustomers();

    Optional<CustomerModel> getCustomerById(Long id);

    CustomerModel saveCustomer(CustomerModel customer);

    void deleteCustomer(Long id);
}

