package com.example.loanapi;

import com.example.loanapi.constant.Role;
import com.example.loanapi.model.CustomerModel;
import com.example.loanapi.model.UserModel;
import com.example.loanapi.repository.CustomerRepository;
import com.example.loanapi.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void init() {
        UserModel userModelA = new UserModel();
        userModelA.setUsername("admin@example.com");
        userModelA.setPassword(passwordEncoder.encode("123"));
        userModelA.setRole(Role.ROLE_ADMIN);
        userRepository.save(userModelA);

        UserModel userModelB = new UserModel();
        userModelB.setUsername("john.doe@example.com");
        userModelB.setPassword(passwordEncoder.encode("123"));
        userModelB.setRole(Role.ROLE_CUSTOMER);

        CustomerModel customerModelB = new CustomerModel();
        customerModelB.setName("John");
        customerModelB.setSurname("Doe");
        customerModelB.setCreditLimit(BigDecimal.valueOf(25000.0));
        customerModelB.setUsedCreditLimit(BigDecimal.ZERO);

        userModelB.setCustomer(customerModelB);
        userRepository.save(userModelB);

        UserModel userModelC = new UserModel();
        userModelC.setUsername("jane.smith@example.com");
        userModelC.setPassword(passwordEncoder.encode("123"));
        userModelC.setRole(Role.ROLE_CUSTOMER);

        CustomerModel customerModelC = new CustomerModel();
        customerModelC.setName("Jane");
        customerModelC.setSurname("Smith");
        customerModelC.setCreditLimit(BigDecimal.valueOf(5000.0));
        customerModelC.setUsedCreditLimit(BigDecimal.ZERO);

        userModelC.setCustomer(customerModelC);
        userRepository.save(userModelC);


    }
}
