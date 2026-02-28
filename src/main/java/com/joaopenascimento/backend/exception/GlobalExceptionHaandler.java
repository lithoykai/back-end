package com.joaopenascimento.backend.exception;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHaandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        String messages = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        StandardError err = new StandardError(Instant.now().toEpochMilli(), status.value(), "Erro de validação", messages, request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<StandardError> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        
        if (e.getMessage().contains("não encontrado")) {
            status = HttpStatus.NOT_FOUND;
        }
        
        StandardError err = new StandardError(Instant.now().toEpochMilli(), status.value(), "Erro na requisição", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardError> handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN; // 403
        StandardError err = new StandardError(Instant.now().toEpochMilli(), status.value(), "Acesso negado", "Você não tem permissão para realizar esta ação", request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
}
