package com.library.library_management.exception;

import com.library.library_management.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(
            ResourceNotFoundException.class
    )
    public ResponseEntity<ErrorResponse>
    handleResourceNotFound(
            ResourceNotFoundException ex
    ) {

        log.error(
                "Resource not found: {}",
                ex.getMessage()
        );

        ErrorResponse response =
                ErrorResponse.builder()
                        .timestamp(
                                LocalDateTime.now()
                        )
                        .status(
                                HttpStatus.NOT_FOUND.value()
                        )
                        .message(
                                ex.getMessage()
                        )
                        .build();

        return new ResponseEntity<>(
                response,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(
            DuplicateResourceException.class
    )
    public ResponseEntity<ErrorResponse>
    handleDuplicateResource(
            DuplicateResourceException ex
    ) {

        log.error(
                "Duplicate resource error: {}",
                ex.getMessage()
        );

        ErrorResponse response =
                ErrorResponse.builder()
                        .timestamp(
                                LocalDateTime.now()
                        )
                        .status(
                                HttpStatus.CONFLICT.value()
                        )
                        .message(
                                ex.getMessage()
                        )
                        .build();

        return new ResponseEntity<>(
                response,
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ErrorResponse>
    handleValidationException(
            MethodArgumentNotValidException ex
    ) {

        log.error(
                "Validation failed: {}",
                ex.getMessage()
        );

        String message =
                ex.getBindingResult()
                        .getFieldError()
                        .getDefaultMessage();

        ErrorResponse response =
                ErrorResponse.builder()
                        .timestamp(
                                LocalDateTime.now()
                        )
                        .status(
                                HttpStatus.BAD_REQUEST.value()
                        )
                        .message(
                                message
                        )
                        .build();

        return ResponseEntity.badRequest()
                .body(response);
    }

    @ExceptionHandler(
            Exception.class
    )
    public ResponseEntity<ErrorResponse>
    handleGenericException(
            Exception ex
    ) {

        log.error(
                "Unexpected error occurred",
                ex
        );

        ErrorResponse response =
                ErrorResponse.builder()
                        .timestamp(
                                LocalDateTime.now()
                        )
                        .status(
                                HttpStatus.INTERNAL_SERVER_ERROR.value()
                        )
                        .message(
                                ex.getMessage()
                        )
                        .build();

        return new ResponseEntity<>(
                response,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}