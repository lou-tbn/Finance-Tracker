package com.dauphine.finance.repository;

import com.dauphine.finance.model.Transaction;
import com.dauphine.finance.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId")
    List<Transaction> findAllByUserId(@Param("userId") UUID userId);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.category.id = :categoryId")
    List<Transaction> findAllByUserIdAndCategoryId(@Param("userId") UUID userId, @Param("categoryId") UUID categoryId);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.date BETWEEN :start AND :end")
    List<Transaction> findAllByUserIdAndDateBetween(@Param("userId") UUID userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.amount BETWEEN :min AND :max")
    List<Transaction> findAllByUserIdAndAmountBetween(@Param("userId") UUID userId, @Param("min") BigDecimal min, @Param("max") BigDecimal max);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.transactionType = :type")
    List<Transaction> findAllByUserIdAndTransactionType(@Param("userId") UUID userId, @Param("type") TransactionType type);
}