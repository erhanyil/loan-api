package com.example.loanapi.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerDTO {
    private Long id;
    private String name;
    private String surname;
    private BigDecimal creditLimit;
    private BigDecimal usedCreditLimit;
}
