package com.neobank.repository;

import com.neobank.entity.CreditPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CreditPaymentRepository extends JpaRepository<CreditPayment, Long> {
    List<CreditPayment> findByCreditId(Long creditId);
    List<CreditPayment> findByPaidFalseAndDueDateBefore(LocalDate date);
}