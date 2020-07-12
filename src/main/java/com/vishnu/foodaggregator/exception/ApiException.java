package com.vishnu.foodaggregator.exception;

import com.vishnu.foodaggregator.enums.ErrorCode;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ApiException {

    private final String errorMessage;
    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;

}
