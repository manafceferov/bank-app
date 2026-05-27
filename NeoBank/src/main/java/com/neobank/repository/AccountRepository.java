package com.neobank.repository;

import com.neobank.entity.Account;
import com.neobank.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserIdAndDeletedFalse(Long userId);
    Optional<Account> findByIbanAndDeletedFalse(String iban);
    Optional<Account> findByIdAndDeletedFalse(Long id);
    boolean existsByIban(String iban);
}