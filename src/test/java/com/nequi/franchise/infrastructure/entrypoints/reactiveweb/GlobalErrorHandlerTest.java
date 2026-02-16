package com.nequi.franchise.infrastructure.entrypoints.reactiveweb;

import com.mongodb.DuplicateKeyException;
import com.nequi.franchise.domain.exception.BusinessException;
import com.nequi.franchise.domain.exception.ResourceNotFoundException;
import com.nequi.franchise.domain.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios para GlobalErrorHandler")
class GlobalErrorHandlerTest {

    @InjectMocks
    private GlobalErrorHandler errorHandler;

    private ServerRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockRequest = MockServerRequest.builder()
                .uri(java.net.URI.create("http://localhost:8080/api/franchises"))
                .build();
    }

    @Test
    @DisplayName("Debe manejar ValidationException con código 400")
    void shouldHandleValidationException() {
        // Arrange
        ValidationException exception = new ValidationException("El nombre no puede estar vacío");

        // Act
        Mono<ServerResponse> result = errorHandler.handleError(exception, mockRequest);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.statusCode());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar ResourceNotFoundException con código 404")
    void shouldHandleResourceNotFoundException() {
        // Arrange
        ResourceNotFoundException exception = new ResourceNotFoundException("Franquicia no encontrada");

        // Act
        Mono<ServerResponse> result = errorHandler.handleError(exception, mockRequest);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.NOT_FOUND, response.statusCode());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar BusinessException con código 409")
    void shouldHandleBusinessException() {
        // Arrange
        BusinessException exception = new BusinessException("Ya existe una sucursal con ese nombre");

        // Act
        Mono<ServerResponse> result = errorHandler.handleError(exception, mockRequest);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.CONFLICT, response.statusCode());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar excepciones genéricas con código 500")
    void shouldHandleGenericException() {
        // Arrange
        RuntimeException exception = new RuntimeException("Error inesperado");

        // Act
        Mono<ServerResponse> result = errorHandler.handleError(exception, mockRequest);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar NullPointerException como error genérico con código 500")
    void shouldHandleNullPointerAsGenericException() {
        // Arrange
        NullPointerException exception = new NullPointerException("Internal error");

        // Act
        Mono<ServerResponse> result = errorHandler.handleError(exception, mockRequest);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar DuplicateKeyException de MongoDB correctamente")
    void shouldHandleMongoDBDuplicateKeyException() {
        // Arrange
        DuplicateKeyException exception = new DuplicateKeyException(
                null, null, null
        );

        // Act
        Mono<ServerResponse> result = errorHandler.handleError(exception, mockRequest);

        // Assert - Verificar que retorna CONFLICT y no otro código
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.CONFLICT, response.statusCode());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar IncorrectResultSizeDataAccessException con código 409")
    void shouldHandleIncorrectResultSizeException() {
        // Arrange
        IncorrectResultSizeDataAccessException exception = new IncorrectResultSizeDataAccessException(
                "Query returned non-unique result", 1
        );

        // Act
        Mono<ServerResponse> result = errorHandler.handleError(exception, mockRequest);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.CONFLICT, response.statusCode());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar datos duplicados en MongoDB con mensaje amigable")
    void shouldHandleDuplicateDataWithFriendlyMessage() {
        // Arrange - Simular el caso real de query con nombre duplicado
        IncorrectResultSizeDataAccessException exception = new IncorrectResultSizeDataAccessException(
                "Query { \"$java\" : Query: { \"name\" : \"McDonalds\"}, Fields: {}, Sort: {} } returned non unique result",
                1
        );

        // Act
        Mono<ServerResponse> result = errorHandler.handleError(exception, mockRequest);

        // Assert - Verificar que retorna CONFLICT para datos duplicados
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.CONFLICT, response.statusCode());
                })
                .verifyComplete();
    }
}

