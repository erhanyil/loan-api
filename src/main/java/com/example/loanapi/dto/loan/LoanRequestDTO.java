package com.example.loanapi.dto.loan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanRequestDTO {
    private Long customerId;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private Integer numberOfInstallments;
}
