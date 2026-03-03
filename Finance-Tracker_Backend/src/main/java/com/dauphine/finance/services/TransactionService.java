package com.dauphine.finance.services;

import com.dauphine.finance.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionService {

    List<Transaction> getAll();

    Transaction getById(UUID id);

    Transaction create(UUID user, BigDecimal amount, LocalDateTime date, Frequency frequency, String description, TransactionType transactionType);

    Transaction update(UUID id, UUID newUser, BigDecimal newAmount, LocalDateTime newDate, Frequency newFrequency, String newDescription, TransactionType newTransactionType);

    Transaction patch(UUID id, UUID newUser, BigDecimal newAmount, LocalDateTime newDate, Frequency newFrequency, String newDescription, TransactionType newTransactionType);

    void deleteById(UUID id);
}
