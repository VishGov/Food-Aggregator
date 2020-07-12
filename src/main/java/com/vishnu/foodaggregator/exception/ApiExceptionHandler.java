package com.vishnu.foodaggregator.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.vishnu.foodaggregator.constants.Constants.ITEM_REQUEST_INVALID_PARAM_NOT_SET;
import static com.vishnu.foodaggregator.enums.ErrorCode.INVALID_ITEM_REQUEST;
import static com.vishnu.foodaggregator.enums.ErrorCode.ITEM_NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
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

    @ExceptionHandler(InvalidItemRequestException.class)
    public ResponseEntity<ApiException> handleInvalidItemRequestException(InvalidItemRequestException e) {
        ApiException apiException = ApiException.builder()
                .errorMessage(e.getMessage())
                .errorCode(INVALID_ITEM_REQUEST)
                .httpStatus(BAD_REQUEST)
                .build();

        return new ResponseEntity<>(apiException, BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiException apiException = ApiException.builder()
                .errorMessage(String.format(ITEM_REQUEST_INVALID_PARAM_NOT_SET, ex.getParameterName()))
                .errorCode(INVALID_ITEM_REQUEST)
                .httpStatus(BAD_REQUEST)
                .build();

        return new ResponseEntity<>(apiException, BAD_REQUEST);
    }

}
