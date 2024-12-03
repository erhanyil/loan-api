package com.example.loanapi.repository;

import com.example.loanapi.model.LoanModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanModel, Long> {
    List<LoanModel> findByCustomer_Id(Long customerId);
    boolean existsByIdAndCustomer_Id(Long id, Long customerId);
}
