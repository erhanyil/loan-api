package com.example.loanapi.service;


import com.example.loanapi.dto.loan.*;
import com.example.loanapi.exception.InvalidLoanParametersException;
import com.example.loanapi.exception.ResourceNotFoundException;
import com.example.loanapi.model.CustomerModel;
import com.example.loanapi.model.LoanInstallmentModel;
import com.example.loanapi.model.LoanModel;
import com.example.loanapi.repository.CustomerRepository;
import com.example.loanapi.repository.LoanInstallmentRepository;
import com.example.loanapi.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface LoanService {

    @Transactional
    LoanDTO createLoan(LoanRequestDTO loanRequestDTO);

    List<LoanDTO> getLoansForCustomer(Long customerId);

    List<LoanInstallmentDTO> getInstallmentsForLoan(Long loanId);

    @Transactional
    PaymentResultDTO payLoan(Long loanId, BigDecimal amount);
}

