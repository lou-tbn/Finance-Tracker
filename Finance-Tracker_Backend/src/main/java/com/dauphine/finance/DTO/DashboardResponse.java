package com.dauphine.finance.DTO;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponse {

    private BigDecimal totalIncomeThisMonth;
    private BigDecimal totalExpenseThisMonth;
    private BigDecimal balance;
    private List<CategoryExpense> expensesByCategory;
    private List<GoalProgress> goalProgresses;

    public DashboardResponse(BigDecimal totalIncomeThisMonth, BigDecimal totalExpenseThisMonth, BigDecimal balance, List<CategoryExpense> expensesByCategory, List<GoalProgress> goalProgresses) {
        this.totalIncomeThisMonth = totalIncomeThisMonth;
        this.totalExpenseThisMonth = totalExpenseThisMonth;
        this.balance = balance;
        this.expensesByCategory = expensesByCategory;
        this.goalProgresses = goalProgresses;
    }

    public BigDecimal getTotalIncomeThisMonth() { return totalIncomeThisMonth; }
    public BigDecimal getTotalExpenseThisMonth() { return totalExpenseThisMonth; }
    public BigDecimal getBalance() { return balance; }
    public List<CategoryExpense> getExpensesByCategory() { return expensesByCategory; }
    public List<GoalProgress> getGoalProgresses() { return goalProgresses; }

    // Classe interne pour les dépenses par catégorie
    public static class CategoryExpense {
        private String categoryName;
        private BigDecimal total;

        public CategoryExpense(String categoryName, BigDecimal total) {
            this.categoryName = categoryName;
            this.total = total;
        }

        public String getCategoryName() { return categoryName; }
        public BigDecimal getTotal() { return total; }
    }

    // Classe interne pour la progression des goals
    public static class GoalProgress {
        private String title;
        private BigDecimal targetAmount;
        private BigDecimal currentAmount;
        private double progressPercentage;

        public GoalProgress(String title, BigDecimal targetAmount, BigDecimal currentAmount) {
            this.title = title;
            this.targetAmount = targetAmount;
            this.currentAmount = currentAmount;
            this.progressPercentage = targetAmount.compareTo(BigDecimal.ZERO) > 0
                    ? currentAmount.divide(targetAmount, 4, java.math.RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                    : 0;
        }

        public String getTitle() { return title; }
        public BigDecimal getTargetAmount() { return targetAmount; }
        public BigDecimal getCurrentAmount() { return currentAmount; }
        public double getProgressPercentage() { return progressPercentage; }
    }
}