package com.example.partidasdefutebol.exceptions;

import com.example.partidasdefutebol.service.ClubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler extends Exception {

    private static final Logger logger = LoggerFactory.getLogger(ClubService.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(CustomException ex) {
        ErrorResponse errorResponse = new ErrorResponse() {

            @Override
            public HttpStatusCode getStatusCode() {
                return null;
            }

            @Override
            public ProblemDetail getBody() {
                return null;
            }
        };
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String errorMessageToUser = "Já existe um registro de mesmo nome para este estado.";
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessageToUser);
    }

    @ExceptionHandler(AmqpRejectAndDontRequeueException.class)
    public ResponseEntity<Map<String, String>> handleAmpqException(AmqpRejectAndDontRequeueException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
