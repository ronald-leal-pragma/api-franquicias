package com.nequi.franchise.infrastructure.entrypoints.reactiveweb;

import com.nequi.franchise.domain.exception.BusinessException;
import com.nequi.franchise.domain.exception.DomainException;
import com.nequi.franchise.domain.exception.ResourceNotFoundException;
import com.nequi.franchise.domain.exception.ValidationException;
import com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Component
public class GlobalErrorHandler {

    public Mono<ServerResponse> handleError(Throwable error, ServerRequest request) {
        log.error("Error procesando request a: {} - Error: {}", request.path(), error.getMessage(), error);

        return switch (error) {
            case ValidationException validationException -> handleValidationException(validationException, request);
            case ResourceNotFoundException resourceNotFoundException ->
                    handleResourceNotFoundException(resourceNotFoundException, request);
            case BusinessException businessException -> handleBusinessException(businessException, request);
            case DomainException domainException -> handleDomainException(domainException, request);
            default -> handleGenericException(request);
        };
    }

    private Mono<ServerResponse> handleValidationException(ValidationException ex, ServerRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.path())
                .build();

        return ServerResponse
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    private Mono<ServerResponse> handleResourceNotFoundException(ResourceNotFoundException ex, ServerRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.path())
                .build();

        return ServerResponse
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    private Mono<ServerResponse> handleBusinessException(BusinessException ex, ServerRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.path())
                .build();

        return ServerResponse
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    private Mono<ServerResponse> handleDomainException(DomainException ex, ServerRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.path())
                .build();

        return ServerResponse
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    private Mono<ServerResponse> handleGenericException(ServerRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("INTERNAL_ERROR")
                .message("Ha ocurrido un error procesando la solicitud")
                .timestamp(LocalDateTime.now())
                .path(request.path())
                .build();

        return ServerResponse
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }
}

