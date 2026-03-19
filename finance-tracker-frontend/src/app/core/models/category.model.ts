export type CategoryType = 'INCOME' | 'EXPENSE';

export interface Category {
  id: string;
  name: string;
  categoryType: CategoryType;
}

export interface CategoryRequest {
  name: string;
  categoryType: CategoryType;
}
