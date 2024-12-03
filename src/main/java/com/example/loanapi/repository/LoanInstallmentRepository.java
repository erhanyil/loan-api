package com.example.loanapi.repository;

import com.example.loanapi.model.LoanInstallmentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallmentModel, Long> {
    List<LoanInstallmentModel> findByLoanId(Long loanId);
}
