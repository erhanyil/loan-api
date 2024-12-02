package com.example.loanapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PaymentResult {
    private int installmentsPaid;
    private BigDecimal amountSpent;
    private boolean loanFullyPaid;
}