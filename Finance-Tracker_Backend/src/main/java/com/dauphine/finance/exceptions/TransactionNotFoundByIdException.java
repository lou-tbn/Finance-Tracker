package com.dauphine.finance.exceptions;

import java.util.UUID;

public class TransactionNotFoundByIdException extends RuntimeException {

    public TransactionNotFoundByIdException(UUID id){
        super("Transaction " + id + " not found");
    }
}
