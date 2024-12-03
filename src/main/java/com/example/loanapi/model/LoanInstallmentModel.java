package com.example.loanapi.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "loan_installments")
public class LoanInstallmentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loan_id")
    @NotNull(message = "Loan is required")
    private LoanModel loan;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Paid amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Paid amount must be 0 or greater")
    private BigDecimal paidAmount;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    private LocalDate paymentDate;

    @NotNull(message = "Paid status is required")
    private Boolean isPaid;
}

