package com.example.loanapi.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanRequestDTO {
    private Long customerId;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private Integer numberOfInstallments;
}
