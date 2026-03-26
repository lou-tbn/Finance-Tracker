package com.dauphine.finance.services;

import com.dauphine.finance.DTO.DashboardResponse;
import com.dauphine.finance.exceptions.UserNotFoundByIdException;
import com.dauphine.finance.model.Goal;
import com.dauphine.finance.repository.GoalRepository;
import com.dauphine.finance.repository.TransactionRepository;
import com.dauphine.finance.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    public DashboardResponse getDashboard(UUID userId, Integer month, Integer year) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));

        LocalDateTime now = LocalDateTime.now();
        int effectiveMonth = month != null ? month : now.getMonthValue();
        int effectiveYear = year != null ? year : now.getYear();


        BigDecimal totalIncome = transactionRepository.sumIncomeByUserAndMonth(userId, effectiveMonth, effectiveYear);
        BigDecimal totalExpense = transactionRepository.sumExpenseByUserAndMonth(userId, effectiveMonth, effectiveYear);

        totalIncome = totalIncome != null ? totalIncome : BigDecimal.ZERO;
        totalExpense = totalExpense != null ? totalExpense : BigDecimal.ZERO;

        // Solde cumulatif : toutes les transactions jusqu'à la fin du mois sélectionné
        LocalDateTime endOfMonth = LocalDate.of(effectiveYear, effectiveMonth, 1)
                .withDayOfMonth(LocalDate.of(effectiveYear, effectiveMonth, 1).lengthOfMonth())
                .atTime(23, 59, 59);

        BigDecimal cumulativeIncome = transactionRepository.sumIncomeCumulativeByUser(userId, endOfMonth);
        BigDecimal cumulativeExpense = transactionRepository.sumExpenseCumulativeByUser(userId, endOfMonth);

        cumulativeIncome = cumulativeIncome != null ? cumulativeIncome : BigDecimal.ZERO;
        cumulativeExpense = cumulativeExpense != null ? cumulativeExpense : BigDecimal.ZERO;

        BigDecimal balance = cumulativeIncome.subtract(cumulativeExpense);


        List<Object[]> rawExpenses = transactionRepository.sumExpensesByCategoryForUserAndMonth(userId, effectiveMonth, effectiveYear);

        List<DashboardResponse.CategoryExpense> expensesByCategory = rawExpenses.stream()
                .map(row -> new DashboardResponse.CategoryExpense(
                        (String) row[0],
                        (BigDecimal) row[1]
                ))
                .toList();

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