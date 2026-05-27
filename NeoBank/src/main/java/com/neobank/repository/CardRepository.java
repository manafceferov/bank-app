package com.neobank.repository;

import com.neobank.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByAccountIdAndDeletedFalse(Long accountId);
    Optional<Card> findByIdAndDeletedFalse(Long id);
    boolean existsByCardNumber(String cardNumber);
    Optional<Card> findByCardNumberAndDeletedFalse(String cardNumber);
}