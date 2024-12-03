package com.example.loanapi.service.impl;

import com.example.loanapi.dto.loan.LoanDTO;
import com.example.loanapi.dto.loan.LoanInstallmentDTO;
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
import com.example.loanapi.service.LoanService;
import com.example.loanapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private static final List<Integer> VALID_INSTALLMENT_NUMBERS = List.of(6, 9, 12, 24);
    private static final BigDecimal MIN_INTEREST_RATE = BigDecimal.valueOf(0.1);
    private static final BigDecimal MAX_INTEREST_RATE = BigDecimal.valueOf(0.5);
    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;
    private final UserService userService;

    /**
     * Creates a new loan for a customer.
     *
     * @param customerId           The ID of the customer
     * @param amount               The loan amount
     * @param interestRate         The interest rate for the loan
     * @param numberOfInstallments The number of installments for the loan
     * @return A DTO representing the created loan
     * @throws ResourceNotFoundException      If the customer is not found
     * @throws InvalidLoanParametersException If the loan parameters are invalid
     */

    @Override
    @Transactional
    public LoanDTO createLoan(LoanRequestDTO loanRequestDTO) {
        CustomerModel customer = customerRepository.findById(loanRequestDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + loanRequestDTO.getCustomerId()));

        validateLoanParameters(customer, loanRequestDTO.getAmount(), loanRequestDTO.getInterestRate(), loanRequestDTO.getNumberOfInstallments());

        BigDecimal totalAmount = calculateTotalAmount(loanRequestDTO.getAmount(), loanRequestDTO.getInterestRate());
        BigDecimal installmentAmount = calculateInstallmentAmount(totalAmount, loanRequestDTO.getNumberOfInstallments());

        LoanModel loan = createAndSaveLoan(customer, loanRequestDTO.getAmount(), loanRequestDTO.getNumberOfInstallments());
        createAndSaveInstallments(loan, installmentAmount, loanRequestDTO.getNumberOfInstallments());

        updateCustomerCreditLimit(customer, loanRequestDTO.getAmount());

        return convertToDTO(loan);
    }

    /**
     * Retrieves all loans for a given customer.
     *
     * @param customerId The ID of the customer
     * @return A list of DTOs representing the customer's loans
     */
    @Override
    public List<LoanDTO> getLoansForCustomer(Long customerId) {
        return loanRepository.findByCustomer_Id(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all installments for a given loan.
     *
     * @param loanId The ID of the loan
     * @return A list of DTOs representing the loan's installments
     */
    @Override
    public List<LoanInstallmentDTO> getInstallmentsForLoan(Long loanId) {
        return loanInstallmentRepository.findByLoanId(loanId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Processes a payment for a loan.
     *
     * @param loanId The ID of the loan
     * @param amount The payment amount
     * @return A PaymentResult object containing information about the payment
     * @throws ResourceNotFoundException If the loan is not found
     */
    @Override
    @Transactional
    public PaymentResultDTO payLoan(Long loanId, BigDecimal amount) {
        LoanModel loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + loanId));

        List<LoanInstallmentModel> installments = loanInstallmentRepository.findByLoanId(loanId);
        BigDecimal remainingAmount = amount;
        int installmentsPaid = 0;

        for (LoanInstallmentModel installment : installments) {
            if (!installment.getIsPaid() && remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal installmentRemaining = installment.getAmount().subtract(installment.getPaidAmount());
                BigDecimal paymentForInstallment = remainingAmount.min(installmentRemaining);

                installment.setPaidAmount(installment.getPaidAmount().add(paymentForInstallment));
                installment.setIsPaid(installment.getPaidAmount().compareTo(installment.getAmount()) >= 0);
                installment.setPaymentDate(LocalDate.now());

                remainingAmount = remainingAmount.subtract(paymentForInstallment);
                installmentsPaid++;
            }
        }

        loanInstallmentRepository.saveAll(installments);

        boolean isLoanFullyPaid = installments.stream().allMatch(LoanInstallmentModel::getIsPaid);
        loan.setIsPaid(isLoanFullyPaid);
        loanRepository.save(loan);

        if (isLoanFullyPaid) {
            updateCustomerCreditLimitAfterPayment(loan.getCustomer(), loan.getLoanAmount());
        }

        return new PaymentResultDTO(installmentsPaid, amount.subtract(remainingAmount), isLoanFullyPaid);
    }

    private void validateLoanParameters(CustomerModel customer, BigDecimal amount, BigDecimal interestRate, Integer numberOfInstallments) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidLoanParametersException("Loan amount must be greater than zero");
        }

        if (interestRate.compareTo(MIN_INTEREST_RATE) < 0 || interestRate.compareTo(MAX_INTEREST_RATE) > 0) {
            throw new InvalidLoanParametersException("Interest rate must be between " + MIN_INTEREST_RATE + " and " + MAX_INTEREST_RATE);
        }

        if (!VALID_INSTALLMENT_NUMBERS.contains(numberOfInstallments)) {
            throw new InvalidLoanParametersException("Number of installments must be one of " + VALID_INSTALLMENT_NUMBERS);
        }

        BigDecimal availableCredit = customer.getCreditLimit().subtract(customer.getUsedCreditLimit());
        if (amount.compareTo(availableCredit) > 0) {
            throw new InvalidLoanParametersException("Loan amount exceeds available credit");
        }
    }

    private BigDecimal calculateTotalAmount(BigDecimal principal, BigDecimal interestRate) {
        return principal.multiply(BigDecimal.ONE.add(interestRate)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateInstallmentAmount(BigDecimal totalAmount, Integer numberOfInstallments) {
        return totalAmount.divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);
    }

    private LoanModel createAndSaveLoan(CustomerModel customer, BigDecimal amount, Integer numberOfInstallments) {
        LoanModel loan = new LoanModel();
        loan.setCustomer(customer);
        loan.setLoanAmount(amount);
        loan.setNumberOfInstallments(numberOfInstallments);
        loan.setCreateDate(LocalDate.now());
        loan.setIsPaid(false);
        return loanRepository.save(loan);
    }

    private void createAndSaveInstallments(LoanModel loan, BigDecimal installmentAmount, Integer numberOfInstallments) {
        List<LoanInstallmentModel> installments = new ArrayList<>();
        LocalDate dueDate = loan.getCreateDate().plusMonths(1);

        for (int i = 0; i < numberOfInstallments; i++) {
            LoanInstallmentModel installment = new LoanInstallmentModel();
            installment.setLoan(loan);
            installment.setAmount(installmentAmount);
            installment.setPaidAmount(BigDecimal.ZERO);
            installment.setDueDate(dueDate);
            installment.setIsPaid(false);
            installments.add(installment);

            dueDate = dueDate.plusMonths(1);
        }

        loanInstallmentRepository.saveAll(installments);
    }

    private void updateCustomerCreditLimit(CustomerModel customer, BigDecimal loanAmount) {
        customer.setUsedCreditLimit(customer.getUsedCreditLimit().add(loanAmount));
        customerRepository.save(customer);
    }

    private void updateCustomerCreditLimitAfterPayment(CustomerModel customer, BigDecimal loanAmount) {
        customer.setUsedCreditLimit(customer.getUsedCreditLimit().subtract(loanAmount));
        customerRepository.save(customer);
    }

    private LoanDTO convertToDTO(LoanModel loan) {
        LoanDTO dto = new LoanDTO();
        dto.setId(loan.getId());
        dto.setCustomerId(loan.getCustomer().getId());
        dto.setLoanAmount(loan.getLoanAmount());
        dto.setNumberOfInstallments(loan.getNumberOfInstallments());
        dto.setCreateDate(loan.getCreateDate());
        dto.setIsPaid(loan.getIsPaid());
        return dto;
    }

    private LoanInstallmentDTO convertToDTO(LoanInstallmentModel installment) {
        LoanInstallmentDTO dto = new LoanInstallmentDTO();
        dto.setId(installment.getId());
        dto.setLoanId(installment.getLoan().getId());
        dto.setAmount(installment.getAmount());
        dto.setPaidAmount(installment.getPaidAmount());
        dto.setDueDate(installment.getDueDate());
        dto.setPaymentDate(installment.getPaymentDate());
        dto.setIsPaid(installment.getIsPaid());
        return dto;
    }

}
