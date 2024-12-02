package com.example.loanapi;

import com.example.loanapi.model.CustomerModel;
import com.example.loanapi.repository.CustomerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final CustomerRepository customerRepository;

    @PostConstruct
    @Transactional
    public void init() {
        CustomerModel customerModelA = new CustomerModel();
        customerModelA.setName("John");
        customerModelA.setSurname("Doe");
        customerModelA.setCreditLimit(BigDecimal.valueOf(15000.0));
        customerModelA.setUsedCreditLimit(BigDecimal.ZERO);
        customerRepository.save(customerModelA);


        CustomerModel customerModelB = new CustomerModel();
        customerModelB.setName("Jane");
        customerModelB.setSurname("Smith");
        customerModelB.setCreditLimit(BigDecimal.valueOf(25000.0));
        customerModelB.setUsedCreditLimit(BigDecimal.ZERO);
        customerRepository.save(customerModelB);


        CustomerModel customerModelC = new CustomerModel();
        customerModelC.setName("Alice");
        customerModelC.setSurname("Johnson");
        customerModelC.setCreditLimit(BigDecimal.valueOf(5000.0));
        customerModelC.setUsedCreditLimit(BigDecimal.ZERO);
        customerRepository.save(customerModelC);


    }
}
