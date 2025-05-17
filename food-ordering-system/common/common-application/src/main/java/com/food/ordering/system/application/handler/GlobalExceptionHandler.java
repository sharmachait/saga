package com.food.ordering.system.application.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDto handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return ErrorDto.builder()
                .message("Unexpected error!")
                .code(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ErrorDto handleException(ValidationException exception) {
        if(exception instanceof ConstraintViolationException){
            String violations = extractViolations((ConstraintViolationException)exception);
            log.error(violations, exception);
            return ErrorDto.builder()
                    .message(violations)
                    .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .build();
        }
        log.error(exception.getMessage(), exception);
        return ErrorDto.builder()
                .message(exception.getMessage())
                .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .build();
    }

    private String extractViolations(ConstraintViolationException exception) {
        StringBuilder sb = new StringBuilder();
        exception.getConstraintViolations()
                .forEach(ex -> sb.append(ex.getMessage()).append("--"));

        return sb.substring(0, sb.toString().length() - 2);
    }
}
