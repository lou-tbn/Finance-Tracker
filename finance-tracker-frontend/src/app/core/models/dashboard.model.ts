export interface CategoryExpense {
  categoryName: string;
  total: number;
}

export interface GoalProgress {
  title: string;
  targetAmount: number;
  currentAmount: number;
  progressPercentage: number;
}

export interface Dashboard {
  totalIncomeThisMonth: number;
  totalExpenseThisMonth: number;
  balance: number;
  expensesByCategory: CategoryExpense[];
  goalProgresses: GoalProgress[];
}

