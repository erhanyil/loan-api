package com.example.loanapi.service.impl;

import com.example.loanapi.model.CustomerModel;
import com.example.loanapi.repository.CustomerRepository;
import com.example.loanapi.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {


    private final CustomerRepository customerRepository;

    @Override
    public List<CustomerModel> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<CustomerModel> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public CustomerModel saveCustomer(CustomerModel customer) {
        return customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}
