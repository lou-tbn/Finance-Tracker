package com.dauphine.finance.exceptions;


public class CategoryNameAlreadyExistException extends RuntimeException {

    public CategoryNameAlreadyExistException(String name){
        super("Category " + name + " already exists.");
    }
}
