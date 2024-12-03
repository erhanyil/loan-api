package com.example.loanapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidLoanParametersException extends RuntimeException {
    public InvalidLoanParametersException(String message) {
        super(message);
    }
}
