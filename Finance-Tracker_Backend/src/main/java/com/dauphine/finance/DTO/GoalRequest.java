package com.dauphine.finance.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class GoalRequest {
    private UUID userId;
    private String title;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private LocalDateTime deadline;

    public UUID getUserId() {
        return userId;
    }
    public String getTitle() {
        return title;
    }
    public BigDecimal getTargetAmount() {
        return targetAmount;
    }
    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }
    public LocalDateTime getDeadline() {
        return deadline;
    }


    public void setUserId(UUID user) {
        this.userId = user;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }
    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
}

