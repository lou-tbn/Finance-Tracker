package com.dauphine.finance.services;

import com.dauphine.finance.DTO.DashboardResponse;
import com.dauphine.finance.exceptions.UserNotFoundByIdException;
import com.dauphine.finance.model.Goal;
import com.dauphine.finance.repository.GoalRepository;
import com.dauphine.finance.repository.TransactionRepository;
import com.dauphine.finance.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final TransactionRepository transactionRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public DashboardServiceImpl(TransactionRepository transactionRepository, GoalRepository goalRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    @Override
    public DashboardResponse getDashboard(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));

        int month = LocalDateTime.now().getMonthValue();
        int year = LocalDateTime.now().getYear();

        // Revenus et dépenses du mois
        BigDecimal totalIncome = transactionRepository.sumIncomeByUserAndMonth(userId, month, year);
        BigDecimal totalExpense = transactionRepository.sumExpenseByUserAndMonth(userId, month, year);

        totalIncome = totalIncome != null ? totalIncome : BigDecimal.ZERO;
        totalExpense = totalExpense != null ? totalExpense : BigDecimal.ZERO;

        BigDecimal balance = totalIncome.subtract(totalExpense);

        // Dépenses par catégorie
        List<Object[]> rawExpenses = transactionRepository.sumExpensesByCategoryForUser(userId);
        List<DashboardResponse.CategoryExpense> expensesByCategory = rawExpenses.stream()
                .map(row -> new DashboardResponse.CategoryExpense(
                        (String) row[0],
                        (BigDecimal) row[1]
                ))
                .toList();

        // Progression des goals
        List<Goal> goals = goalRepository.findAllByUserId(userId);
        List<DashboardResponse.GoalProgress> goalProgresses = goals.stream()
                .map(g -> new DashboardResponse.GoalProgress(
                        g.getTitle(),
                        g.getTargetAmount(),
                        g.getCurrentAmount()
                ))
                .toList();

        return new DashboardResponse(totalIncome, totalExpense, balance, expensesByCategory, goalProgresses);
    }
}