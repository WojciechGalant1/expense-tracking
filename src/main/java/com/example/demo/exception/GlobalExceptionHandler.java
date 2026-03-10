package com.example.demo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TransactionNotFoundException.class)
    public ModelAndView handleTransactionNotFoundException(TransactionNotFoundException ex) {
        logger.error("Transaction not found: {}", ex.getMessage());
        
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorTitle", "Transaction Not Found");
        modelAndView.addObject("errorMessage", ex.getMessage());
        modelAndView.addObject("errorCode", "404");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        
        return modelAndView;
    }

    @ExceptionHandler(InvalidTransactionDataException.class)
    public ModelAndView handleInvalidTransactionDataException(InvalidTransactionDataException ex) {
        logger.error("Invalid transaction data: {}", ex.getMessage());
        
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorTitle", "Invalid Transaction Data");
        modelAndView.addObject("errorMessage", ex.getMessage());
        modelAndView.addObject("errorCode", "400");
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        
        return modelAndView;
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ModelAndView handleDatabaseOperationException(DatabaseOperationException ex) {
        logger.error("Database operation failed: {}", ex.getMessage());
        
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorTitle", "Database Error");
        modelAndView.addObject("errorMessage", "A database error occurred. Please try again later.");
        modelAndView.addObject("errorCode", "500");
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        
        return modelAndView;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ModelAndView handleValidationException(MethodArgumentNotValidException ex) {
        logger.error("Validation error: {}", ex.getMessage());
        
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errorMessage.append(error.getField()).append(" ").append(error.getDefaultMessage()).append("; ")
        );
        
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorTitle", "Validation Error");
        modelAndView.addObject("errorMessage", errorMessage.toString());
        modelAndView.addObject("errorCode", "400");
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        
        return modelAndView;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ModelAndView handleConstraintViolationException(ConstraintViolationException ex) {
        logger.error("Constraint violation: {}", ex.getMessage());
        
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorTitle", "Data Validation Error");
        modelAndView.addObject("errorMessage", ex.getMessage());
        modelAndView.addObject("errorCode", "400");
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred: ", ex);
        
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorTitle", "Internal Server Error");
        modelAndView.addObject("errorMessage", "An unexpected error occurred. Please try again later.");
        modelAndView.addObject("errorCode", "500");
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        
        return modelAndView;
    }

    // Note: API endpoint exception handlers removed to avoid conflicts with web MVC handlers
    // If you need REST API endpoints in the future, create a separate @RestControllerAdvice class
}
