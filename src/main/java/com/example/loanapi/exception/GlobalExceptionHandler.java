package com.example.loanapi.exception;

import com.example.loanapi.dto.BaseResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponseDTO> resourceNotFoundException(ResourceNotFoundException ex) {
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateResponse(ex.getMessage(), HttpStatus.NOT_FOUND));

    }

    @ExceptionHandler(InvalidLoanParametersException.class)
    public ResponseEntity<BaseResponseDTO> invalidLoanParametersException(InvalidLoanParametersException ex) {
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(generateResponse(ex.getMessage(), HttpStatus.BAD_REQUEST));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseDTO> globalExceptionHandler(Exception ex) {
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(generateResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }


    BaseResponseDTO generateResponse(String message, HttpStatus httpStatus){
        return BaseResponseDTO.builder().msg(message).success(false).build();
    }


}
