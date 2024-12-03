package com.example.loanapi.dto.loan;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PaymentResultDTO {
    private int installmentsPaid;
    private BigDecimal amountSpent;
    private boolean loanFullyPaid;
}