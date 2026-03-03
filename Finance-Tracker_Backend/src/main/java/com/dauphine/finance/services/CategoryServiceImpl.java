package com.dauphine.finance.services;

import com.dauphine.finance.exceptions.CategoryNameAlreadyExistException;
import com.dauphine.finance.exceptions.CategoryNotFoundByIdException;
import com.dauphine.finance.model.Category;
import com.dauphine.finance.model.CategoryType;
import com.dauphine.finance.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    public CategoryServiceImpl(CategoryRepository repository){
        this.repository = repository;
    }

    @Override
    public List<Category> getAll(){
        return repository.findAll();
    }

    @Override
    public List<Category> getAllLikeName(String name){
        return repository.findAllLikeName(name);
    }

    @Override
    public Category getById(UUID id) throws CategoryNotFoundByIdException {
        return repository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundByIdException(id));
    }
    @Override
    public Category create(String name, CategoryType type){

        if (repository.existsByName(name)) {
            throw new CategoryNameAlreadyExistException(name);
        }

        Category category = new Category(name, type);
        return repository.save(category);
    }

    @Override
    public Category update(UUID id, String newName, CategoryType newType){
        Category category = getById(id);
        if (repository.existsByName(newName) && !newName.equalsIgnoreCase(category.getName())) {
            throw new CategoryNameAlreadyExistException(newName);
        }
        category.setName(newName);
        category.setCategoryType(newType);
        return repository.save(category);
    }

    @Override
    public Category patch(UUID id, String newName, CategoryType newType){
        Category category = getById(id);

        if(newName != null) {
            if (repository.existsByName(newName) && !(newName.equalsIgnoreCase(category.getName()))) {
                throw new CategoryNameAlreadyExistException(newName);
            }
            else{
                category.setName(newName);
            }
        }

        if(newType != null){
            category.setCategoryType(newType);
        }

        return repository.save(category);
    }

    @Override
    public void deleteById(UUID id){
        Category category = getById(id);
        repository.delete(category);
    }

}
