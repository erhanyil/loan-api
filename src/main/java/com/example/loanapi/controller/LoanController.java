package com.example.loanapi.controller;


import com.example.loanapi.dto.loan.*;
import com.example.loanapi.service.LoanService;
import com.example.loanapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanDTO> createLoan(@RequestBody LoanRequestDTO loanRequestDTO) {
        LoanDTO loan = loanService.createLoan(loanRequestDTO);
        return ResponseEntity.ok(loan);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @userService.checkValidForCustomer(#customerId))")
    public ResponseEntity<List<LoanDTO>> getLoansForCustomer(@PathVariable Long customerId) {
        List<LoanDTO> loans = loanService.getLoansForCustomer(customerId);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/{loanId}/installments")
    @PreAuthorize("hasAnyRole('ADMIN') or (hasRole('CUSTOMER') and @userService.checkLoanExistForCustomer(#loanId))")
    public ResponseEntity<List<LoanInstallmentDTO>> getInstallmentsForLoan(@PathVariable Long loanId) {
        List<LoanInstallmentDTO> installments = loanService.getInstallmentsForLoan(loanId);
        return ResponseEntity.ok(installments);
    }

    @PostMapping("/{loanId}/pay")
    @PreAuthorize("hasAnyRole('ADMIN') or (hasRole('CUSTOMER') and @userService.checkLoanExistForCustomer(#loanId))")
    public ResponseEntity<PaymentResultDTO> payLoan(@PathVariable Long loanId, @RequestBody PaymentRequestDTO paymentRequestDTO) {
        PaymentResultDTO result = loanService.payLoan(loanId, paymentRequestDTO.getAmount());
        return ResponseEntity.ok(result);
    }
}
