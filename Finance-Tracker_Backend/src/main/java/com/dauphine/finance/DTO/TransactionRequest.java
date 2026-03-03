package com.dauphine.finance.DTO;

import com.dauphine.finance.model.Category;
import com.dauphine.finance.model.Frequency;
import com.dauphine.finance.model.TransactionType;
import com.dauphine.finance.model.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionRequest {
    private UUID user;
    private Category category;
    private BigDecimal amount;
    private LocalDateTime date;
    private Frequency frequency;
    private String description;
    private TransactionType transactionType;

    public UUID getUser() {
        return user;
    }
    public Category getCategory() {
        return category;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public LocalDateTime getDate() {
        return date;
    }
    public Frequency getFrequency() {
        return frequency;
    }
    public String getDescription() {
        return description;
    }
    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setUser(UUID user) {
        this.user = user;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}
