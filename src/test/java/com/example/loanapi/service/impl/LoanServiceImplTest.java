package com.example.loanapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.example.loanapi.dto.loan.LoanDTO;
import com.example.loanapi.dto.loan.LoanRequestDTO;
import com.example.loanapi.dto.loan.PaymentResultDTO;
import com.example.loanapi.exception.InvalidLoanParametersException;
import com.example.loanapi.exception.ResourceNotFoundException;
import com.example.loanapi.model.CustomerModel;
import com.example.loanapi.model.LoanInstallmentModel;
import com.example.loanapi.model.LoanModel;
import com.example.loanapi.repository.CustomerRepository;
import com.example.loanapi.repository.LoanInstallmentRepository;
import com.example.loanapi.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class LoanServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    private CustomerModel customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customer = new CustomerModel();
        customer.setId(1L);
        customer.setCreditLimit(BigDecimal.valueOf(10000));
        customer.setUsedCreditLimit(BigDecimal.ZERO);
    }

    @Test
    void testCreateLoan_Success() {
        // Arrange
        LoanRequestDTO loanRequestDTO = new LoanRequestDTO();
        loanRequestDTO.setCustomerId(1L);
        loanRequestDTO.setAmount(BigDecimal.valueOf(5000));
        loanRequestDTO.setInterestRate(BigDecimal.valueOf(0.2));
        loanRequestDTO.setNumberOfInstallments(12);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // Create a LoanModel with a valid createDate
        LoanModel loan = new LoanModel();
        loan.setId(1L);
        loan.setCustomer(customer);
        loan.setLoanAmount(BigDecimal.valueOf(5000));
        loan.setNumberOfInstallments(12);
        loan.setCreateDate(LocalDate.now()); // Set a valid createDate
        loan.setIsPaid(false);

        when(loanRepository.save(any(LoanModel.class))).thenReturn(loan);

        // Act
        LoanDTO loanDTO = loanService.createLoan(loanRequestDTO);

        // Assert
        assertNotNull(loanDTO);
        assertEquals(loanDTO.getCustomerId(), customer.getId());
        assertEquals(loanDTO.getLoanAmount(), BigDecimal.valueOf(5000));
    }

    @Test
    void testCreateLoan_CustomerNotFound() {
        // Arrange
        LoanRequestDTO loanRequestDTO = new LoanRequestDTO();
        loanRequestDTO.setCustomerId(1L);
        loanRequestDTO.setAmount(BigDecimal.valueOf(5000));
        loanRequestDTO.setInterestRate(BigDecimal.valueOf(0.2));
        loanRequestDTO.setNumberOfInstallments(12);

        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> loanService.createLoan(loanRequestDTO));
    }

    @Test
    void testCreateLoan_InvalidLoanParameters() {
        // Arrange
        LoanRequestDTO loanRequestDTO = new LoanRequestDTO();
        loanRequestDTO.setCustomerId(1L);
        loanRequestDTO.setAmount(BigDecimal.valueOf(-5000));  // Invalid amount
        loanRequestDTO.setInterestRate(BigDecimal.valueOf(0.2));
        loanRequestDTO.setNumberOfInstallments(12);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // Act & Assert
        assertThrows(InvalidLoanParametersException.class, () -> loanService.createLoan(loanRequestDTO));
    }

    @Test
    void testPayLoan_Success() {
        // Arrange
        LoanModel loan = new LoanModel();
        loan.setId(1L);
        loan.setCustomer(customer);
        loan.setLoanAmount(BigDecimal.valueOf(5000));
        loan.setNumberOfInstallments(12);
        loan.setIsPaid(false);

        // Mock loan installments
        LoanInstallmentModel installment = new LoanInstallmentModel();
        installment.setId(1L);
        installment.setLoan(loan);
        installment.setAmount(BigDecimal.valueOf(500));
        installment.setPaidAmount(BigDecimal.ZERO);
        installment.setIsPaid(false);
        installment.setDueDate(LocalDate.now().plusMonths(1));

        List<LoanInstallmentModel> installments = new ArrayList<>();
        installments.add(installment);

        // Mock the repositories
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findByLoanId(1L)).thenReturn(installments);

        // Act
        PaymentResultDTO paymentResult = loanService.payLoan(1L, BigDecimal.valueOf(5000));

        // Assert
        assertNotNull(paymentResult);
        assertTrue(paymentResult.isLoanFullyPaid());
        assertEquals(BigDecimal.valueOf(500), paymentResult.getAmountSpent());
    }

    @Test
    void testPayLoan_LoanNotFound() {
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> loanService.payLoan(1L, BigDecimal.valueOf(5000)));
    }

    @Test
    void testCreateLoan_ExceedsCreditLimit() {
        // Arrange
        LoanRequestDTO loanRequestDTO = new LoanRequestDTO();
        loanRequestDTO.setCustomerId(1L);
        loanRequestDTO.setAmount(BigDecimal.valueOf(15000));  // Exceeds credit limit
        loanRequestDTO.setInterestRate(BigDecimal.valueOf(0.2));
        loanRequestDTO.setNumberOfInstallments(12);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // Act & Assert
        assertThrows(InvalidLoanParametersException.class, () -> loanService.createLoan(loanRequestDTO));
    }

    @Test
    void testPayLoan_PartialPayment() {
        // Arrange
        LoanModel loan = new LoanModel();
        loan.setId(1L);
        loan.setCustomer(customer);
        loan.setLoanAmount(BigDecimal.valueOf(5000));
        loan.setNumberOfInstallments(12);
        loan.setIsPaid(false);

        LoanInstallmentModel installment = new LoanInstallmentModel();
        installment.setId(1L);
        installment.setLoan(loan);
        installment.setAmount(BigDecimal.valueOf(500));
        installment.setPaidAmount(BigDecimal.ZERO);
        installment.setIsPaid(false);
        installment.setDueDate(LocalDate.now().plusMonths(1));

        List<LoanInstallmentModel> installments = new ArrayList<>();
        installments.add(installment);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findByLoanId(1L)).thenReturn(installments);

        // Act
        PaymentResultDTO paymentResult = loanService.payLoan(1L, BigDecimal.valueOf(1000));  // Partial payment

        // Assert
        assertNotNull(paymentResult);
        assertTrue(paymentResult.isLoanFullyPaid());
        assertEquals(BigDecimal.valueOf(500), paymentResult.getAmountSpent());
    }

    @Test
    void testPayLoan_Overpayment() {
        // Arrange
        LoanModel loan = new LoanModel();
        loan.setId(1L);
        loan.setCustomer(customer);
        loan.setLoanAmount(BigDecimal.valueOf(5000));
        loan.setNumberOfInstallments(12);
        loan.setIsPaid(false);

        LoanInstallmentModel installment = new LoanInstallmentModel();
        installment.setId(1L);
        installment.setLoan(loan);
        installment.setAmount(BigDecimal.valueOf(500));
        installment.setPaidAmount(BigDecimal.ZERO);
        installment.setIsPaid(false);
        installment.setDueDate(LocalDate.now().plusMonths(1));

        List<LoanInstallmentModel> installments = new ArrayList<>();
        installments.add(installment);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findByLoanId(1L)).thenReturn(installments);

        // Act
        PaymentResultDTO paymentResult = loanService.payLoan(1L, BigDecimal.valueOf(6000));  // Overpayment

        // Assert
        assertNotNull(paymentResult);
        assertTrue(paymentResult.isLoanFullyPaid());
        assertEquals(BigDecimal.valueOf(500), paymentResult.getAmountSpent());  // Should be capped at loan amount
    }

    @Test
    void testPayLoan_InvalidPaymentAmount() {
        // Arrange
        LoanModel loan = new LoanModel();
        loan.setId(1L);
        loan.setCustomer(customer);
        loan.setLoanAmount(BigDecimal.valueOf(5000));
        loan.setNumberOfInstallments(12);
        loan.setIsPaid(false);

        LoanInstallmentModel installment = new LoanInstallmentModel();
        installment.setId(1L);
        installment.setLoan(loan);
        installment.setAmount(BigDecimal.valueOf(500));
        installment.setPaidAmount(BigDecimal.ZERO);
        installment.setIsPaid(false);
        installment.setDueDate(LocalDate.now().plusMonths(1));

        List<LoanInstallmentModel> installments = new ArrayList<>();
        installments.add(installment);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findByLoanId(1L)).thenReturn(installments);

        // Act & Assert
        assertThrows(InvalidLoanParametersException.class, () -> loanService.payLoan(1L, BigDecimal.valueOf(-500)));  // Invalid payment amount
    }

    @Test
    void testCreateLoan_InvalidInstallments() {
        // Arrange
        LoanRequestDTO loanRequestDTO = new LoanRequestDTO();
        loanRequestDTO.setCustomerId(1L);
        loanRequestDTO.setAmount(BigDecimal.valueOf(5000));
        loanRequestDTO.setInterestRate(BigDecimal.valueOf(0.2));
        loanRequestDTO.setNumberOfInstallments(0);  // Invalid number of installments

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // Act & Assert
        assertThrows(InvalidLoanParametersException.class, () -> loanService.createLoan(loanRequestDTO));
    }

    @Test
    void testCreateLoan_ZeroAmount() {
        // Arrange
        LoanRequestDTO loanRequestDTO = new LoanRequestDTO();
        loanRequestDTO.setCustomerId(1L);
        loanRequestDTO.setAmount(BigDecimal.ZERO);  // Invalid loan amount
        loanRequestDTO.setInterestRate(BigDecimal.valueOf(0.2));
        loanRequestDTO.setNumberOfInstallments(12);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // Act & Assert
        assertThrows(InvalidLoanParametersException.class, () -> loanService.createLoan(loanRequestDTO));
    }
}
