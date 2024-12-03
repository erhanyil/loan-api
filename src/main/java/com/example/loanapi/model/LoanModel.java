package com.example.loanapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "loans")
public class LoanModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @NotNull(message = "Customer is required")
    private CustomerModel customer;

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Loan amount must be greater than 0")
    private BigDecimal loanAmount;

    @NotNull(message = "Number of installments is required")
    @Min(value = 6, message = "Number of installments must be at least 6")
    @Max(value = 24, message = "Number of installments must be at most 24")
    private Integer numberOfInstallments;

    @NotNull(message = "Create date is required")
    private LocalDate createDate;

    @NotNull(message = "Paid status is required")
    private Boolean isPaid;
}

