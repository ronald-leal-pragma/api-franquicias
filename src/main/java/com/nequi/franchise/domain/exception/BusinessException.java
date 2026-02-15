package com.nequi.franchise.domain.exception;

public class BusinessException extends DomainException {
    public BusinessException(String message) {
        super("BUSINESS_ERROR", message);
    }

    public BusinessException(String code, String message) {
        super(code, message);
    }
}

