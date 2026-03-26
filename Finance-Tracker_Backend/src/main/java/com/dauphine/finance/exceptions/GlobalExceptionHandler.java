package com.dauphine.finance.exceptions;


import com.dauphine.finance.model.Goal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            CategoryNotFoundByIdException.class,
            GoalNotFoundByIdException.class,
            UserNotFoundByIdException.class,
            TransactionNotFoundByIdException.class
    })
    public ResponseEntity<String> handleNotFoundException(RuntimeException ex) {

        logger.warn("[NOT FOUND] {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler({
            CategoryNameAlreadyExistException.class,
            UsernameAlreadyExistException.class,
            EmailAlreadyExistException.class
    })
    public ResponseEntity<String> handleAlreadyExistsException(RuntimeException ex) {

        logger.warn("[ALREADY EXISTS] {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("[BAD REQUEST] {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
