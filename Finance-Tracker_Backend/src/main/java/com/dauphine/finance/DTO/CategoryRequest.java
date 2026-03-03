package com.dauphine.finance.DTO;

import com.dauphine.finance.model.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CategoryRequest {
    private String name;
    private CategoryType categoryType;

    @NotBlank
    public String getName() { return name; }

    @NotNull
    public CategoryType getCategoryType() { return categoryType; }

    public void setName(String name) { this.name = name; }
    public void setCategoryType(CategoryType categoryType) { this.categoryType = categoryType; }
}
