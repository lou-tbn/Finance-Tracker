package com.dauphine.finance.exceptions;

public class EmailAlreadyExistException extends RuntimeException {

    public EmailAlreadyExistException(String email){
        super("Email " + email + " already exists.");
    }
}

