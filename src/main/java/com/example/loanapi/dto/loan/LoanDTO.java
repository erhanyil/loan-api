package com.example.loanapi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanDTO {
    private Long id;
    private Long customerId;
    private BigDecimal loanAmount;
    private Integer numberOfInstallments;
    private LocalDate createDate;
    private Boolean isPaid;
}
