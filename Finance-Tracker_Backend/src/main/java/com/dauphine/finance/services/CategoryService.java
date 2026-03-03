package com.dauphine.finance.services;


import com.dauphine.finance.model.Category;
import com.dauphine.finance.model.CategoryType;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    List<Category> getAll();

    List<Category> getAllLikeName(String name);

    Category getById(UUID id);

    Category create(String name, CategoryType type);

    Category update(UUID id, String name, CategoryType newType);

    Category patch(UUID id, String newName, CategoryType newType);

    void deleteById(UUID id);
}
