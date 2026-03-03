package com.dauphine.finance.exceptions;

import java.util.UUID;

public class CategoryNotFoundByIdException extends RuntimeException {

    public CategoryNotFoundByIdException(UUID id){
        super("Category " + id + " not found");
    }
}
