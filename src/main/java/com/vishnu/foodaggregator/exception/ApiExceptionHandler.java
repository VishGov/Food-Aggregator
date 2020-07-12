package com.vishnu.foodaggregator.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.vishnu.foodaggregator.enums.ErrorCode.ITEM_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ApiException> handleItemNotFoundException(ItemNotFoundException e) {
        ApiException apiException = ApiException.builder()
                .errorMessage(e.getMessage())
                .errorCode(ITEM_NOT_FOUND)
                .httpStatus(NOT_FOUND)
                .build();

        return new ResponseEntity<>(apiException, NOT_FOUND);
    }

}
