package com.dauphine.finance.exceptions;

import java.util.UUID;

public class UserNotFoundByIdException extends RuntimeException {

    public UserNotFoundByIdException(UUID id){
        super("User " + id + " not found");
    }
}