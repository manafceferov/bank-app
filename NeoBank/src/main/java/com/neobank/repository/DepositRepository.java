package com.neobank.repository;

import com.neobank.entity.Deposit;
import com.neobank.enums.DepositStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {
    List<Deposit> findByAccountIdAndDeletedFalse(Long accountId);
    List<Deposit> findByStatusAndDeletedFalse(DepositStatus status);
    Optional<Deposit> findByIdAndDeletedFalse(Long id);
}