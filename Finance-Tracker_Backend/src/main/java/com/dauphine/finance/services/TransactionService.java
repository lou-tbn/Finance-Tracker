package com.dauphine.finance.services;

import com.dauphine.finance.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionService {

    List<Transaction> getAllByUserId(UUID userId);

    List<Transaction> getAllByUserIdAndCategoryId(UUID userId, UUID categoryId);

    List<Transaction> getAllByUserIdAndDateBetween(UUID userId, LocalDateTime start, LocalDateTime end);

    List<Transaction> generateRecurringTransactions(UUID userId);

    List<Transaction> getAllByUserIdAndAmountBetween(UUID userId, BigDecimal min, BigDecimal max);

    List<Transaction> getAllByUserIdAndTransactionType(UUID userId, TransactionType type);


    List<Transaction> getAll();

    List<Transaction> getAllWithFilters(
            UUID userId,
            UUID categoryId,
            LocalDateTime start,
            LocalDateTime end,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            TransactionType transactionType,
            String search
    );

    Transaction getById(UUID id);

    Transaction create(UUID userId, UUID categoryId, BigDecimal amount, LocalDateTime date, Frequency frequency, String description, TransactionType transactionType);

    Transaction update(UUID id, UUID newUserId, UUID newCategoryId, BigDecimal newAmount, LocalDateTime newDate, Frequency newFrequency, String newDescription, TransactionType newTransactionType);

    Transaction patch(UUID id, UUID newUserId, UUID newCategoryId, BigDecimal newAmount, LocalDateTime newDate, Frequency newFrequency, String newDescription, TransactionType newTransactionType);

    void deleteById(UUID id);
}
