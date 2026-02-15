package com.nequi.franchise.domain.exception;

public class ValidationException extends DomainException {
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }

    public ValidationException(String code, String message) {
        super(code, message);
    }
}

