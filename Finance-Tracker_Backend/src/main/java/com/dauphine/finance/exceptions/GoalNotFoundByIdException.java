package com.dauphine.finance.exceptions;

import java.util.UUID;

public class GoalNotFoundByIdException extends RuntimeException {

    public GoalNotFoundByIdException(UUID id){
        super("Goal " + id + " not found");
    }
}
