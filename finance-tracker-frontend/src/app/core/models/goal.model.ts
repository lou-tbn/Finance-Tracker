export interface Goal {
  id: string;
  user?: { id: string };
  title: string;
  targetAmount: number;
  currentAmount: number;
  deadline?: string | null;
}

export interface GoalRequest {
  userId: string;
  title: string;
  targetAmount: number;
  currentAmount: number;
  deadline?: string | null;
}
