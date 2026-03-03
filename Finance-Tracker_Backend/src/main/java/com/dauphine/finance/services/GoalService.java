package com.dauphine.finance.services;

import com.dauphine.finance.model.CategoryType;
import com.dauphine.finance.model.Goal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface GoalService {
    List<Goal> getAll();

    List<Goal> getAllLikeTitle(String title);

    Goal getById(UUID id);

    Goal create(UUID userId, String title, BigDecimal targetAmount, BigDecimal currentAmount, LocalDateTime deadline);

    Goal update(UUID id, UUID newUserID, String newTitle, BigDecimal newTargetAmount, BigDecimal newCurrentAmount, LocalDateTime newDeadline);

    Goal patch(UUID id, UUID newUserID, String newTitle, BigDecimal newTargetAmount, BigDecimal newCurrentAmount, LocalDateTime newDeadline);

    void deleteById(UUID id);
}
