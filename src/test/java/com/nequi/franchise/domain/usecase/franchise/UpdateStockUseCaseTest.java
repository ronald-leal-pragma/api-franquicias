package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios para UpdateStockUseCase")
class UpdateStockUseCaseTest {

    @Mock
    private FranchiseGateway gateway;

    @InjectMocks
    private UpdateStockUseCase updateStockUseCase;

    private Franchise franchise;
    private String franchiseId;
    private String branchName;
    private String productName;

    @BeforeEach
    void setUp() {
        franchiseId = "franchise-1";
        branchName = "Sucursal Centro";
        productName = "Producto Test";

        franchise = Franchise.builder()
                .id(franchiseId)
                .name("Franquicia Test")
                .build();
    }

    @Test
    @DisplayName("Debe actualizar el stock exitosamente con un valor positivo")
    void shouldUpdateStockSuccessfully() {
        // Arrange
        Integer newStock = 150;
        when(gateway.updateStock(eq(franchiseId), eq(branchName), eq(productName), eq(newStock)))
                .thenReturn(Mono.just(franchise));

        // Act
        Mono<Franchise> result = updateStockUseCase.apply(franchiseId, branchName, productName, newStock);

        // Assert
        StepVerifier.create(result)
                .expectNext(franchise)
                .verifyComplete();

        verify(gateway, times(1)).updateStock(franchiseId, branchName, productName, newStock);
    }

    @Test
    @DisplayName("Debe actualizar el stock exitosamente con valor cero")
    void shouldUpdateStockSuccessfullyWhenZero() {
        // Arrange
        Integer newStock = 0;
        when(gateway.updateStock(eq(franchiseId), eq(branchName), eq(productName), eq(newStock)))
                .thenReturn(Mono.just(franchise));

        // Act
        Mono<Franchise> result = updateStockUseCase.apply(franchiseId, branchName, productName, newStock);

        // Assert
        StepVerifier.create(result)
                .expectNext(franchise)
                .verifyComplete();

        verify(gateway, times(1)).updateStock(franchiseId, branchName, productName, newStock);
    }

    @Test
    @DisplayName("Debe lanzar error cuando el stock es negativo")
    void shouldThrowErrorWhenStockIsNegative() {
        // Arrange
        Integer newStock = -1;

        // Act
        Mono<Franchise> result = updateStockUseCase.apply(franchiseId, branchName, productName, newStock);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Stock cannot be negative"))
                .verify();

        verify(gateway, never()).updateStock(anyString(), anyString(), anyString(), anyInt());
    }

    @Test
    @DisplayName("Debe lanzar error cuando el stock es un numero negativo grande")
    void shouldThrowErrorWhenStockIsLargeNegative() {
        // Arrange
        Integer newStock = -999;

        // Act
        Mono<Franchise> result = updateStockUseCase.apply(franchiseId, branchName, productName, newStock);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Stock cannot be negative"))
                .verify();

        verify(gateway, never()).updateStock(anyString(), anyString(), anyString(), anyInt());
    }
}

