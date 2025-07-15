package com.underrRndezvous.backend.controller;

import com.underrRndezvous.backend.controller.dto.response.CommonErrorResponse;
import com.underrRndezvous.backend.controller.dto.response.CommonValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonValidationError validationExceptionHandler(BindException e) {
        CommonValidationError errorResponse = new CommonValidationError("400", "validation 오류 입니다.");

        for (FieldError error : e.getFieldErrors()) {
            errorResponse.addValidation(error.getField(), error.getDefaultMessage());
        }

        return errorResponse;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public CommonErrorResponse runtimeExceptionHandler(RuntimeException e) {
        return new CommonErrorResponse("400",e.toString());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Error.class)
    public CommonErrorResponse serverErrorHandler(Error e) {
        return new CommonErrorResponse("500", e.toString());
    }

}
