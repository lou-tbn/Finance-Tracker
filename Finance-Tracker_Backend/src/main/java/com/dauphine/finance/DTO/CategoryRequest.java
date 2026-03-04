package com.dauphine.finance.DTO;

import com.dauphine.finance.model.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CategoryRequest {

    @NotBlank
    private String name;

    @NotNull
    private CategoryType categoryType;

    public String getName() { return name; }

    public CategoryType getCategoryType() { return categoryType; }

    public void setName(String name) { this.name = name; }
    public void setCategoryType(CategoryType categoryType) { this.categoryType = categoryType; }
}
