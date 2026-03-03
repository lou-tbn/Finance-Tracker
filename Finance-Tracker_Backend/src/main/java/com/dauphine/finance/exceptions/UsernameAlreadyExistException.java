package com.dauphine.finance.exceptions;

public class UsernameAlreadyExistException extends RuntimeException {

    public UsernameAlreadyExistException(String name){
        super("Username :  " + name + " already exists.");
    }
}

