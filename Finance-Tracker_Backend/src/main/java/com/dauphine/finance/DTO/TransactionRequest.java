package com.dauphine.finance.DTO;

import com.dauphine.finance.model.Frequency;
import com.dauphine.finance.model.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionRequest {

    @NotNull
    private UUID userId;

    private UUID categoryId;  // optionnel, une transaction peut ne pas avoir de catégorie

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private LocalDateTime date;

    private Frequency frequency;

    private String description;

    @NotNull
    private TransactionType transactionType;

    public UUID getUserId() { return userId; }
    public UUID getCategoryId() { return categoryId; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getDate() { return date; }
    public Frequency getFrequency() { return frequency; }
    public String getDescription() { return description; }
    public TransactionType getTransactionType() { return transactionType; }

    public void setUserId(UUID userId) { this.userId = userId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public void setFrequency(Frequency frequency) { this.frequency = frequency; }
    public void setDescription(String description) { this.description = description; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }
}