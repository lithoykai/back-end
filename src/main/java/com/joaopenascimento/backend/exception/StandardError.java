package com.joaopenascimento.backend.exception;

public record StandardError(
    Long timestamp,
    Integer status,
    String error,
    String message,
    String path) {}
