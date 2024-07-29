package com.panicathe.account.exception;

import com.panicathe.account.dto.ErrorResponse;
import com.panicathe.account.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountException.class)
    public ErrorResponse handleAccountException(AccountException e){
        log.error("{} is occurred.", e.getErrorCode());
        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("DataIntegrityViolationException occurred.", e);
        return new ErrorResponse(ErrorCode.INVALID_REQUEST,
                ErrorCode.INVALID_REQUEST.getDescription());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e){
        log.error("Data integrity violation.", e);
        return new ErrorResponse(ErrorCode.INVALID_REQUEST,
                ErrorCode.INVALID_REQUEST.getDescription());
    }


    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(AccountException e){
        log.error("Exception is occurred.", e);
        return new ErrorResponse(
                ErrorCode.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.getDescription());
    }
}
