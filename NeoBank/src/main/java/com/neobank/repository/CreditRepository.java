package com.neobank.repository;

import com.neobank.entity.Credit;
import com.neobank.enums.CreditStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {
    List<Credit> findByUserIdAndDeletedFalse(Long userId);
    List<Credit> findByStatusAndDeletedFalse(CreditStatus status);
    Optional<Credit> findByIdAndDeletedFalse(Long id);
    List<Credit> findByAccountIdAndDeletedFalse(Long accountId);
}