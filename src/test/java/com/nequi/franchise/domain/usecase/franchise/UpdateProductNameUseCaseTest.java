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
@DisplayName("Tests unitarios para UpdateProductNameUseCase")
class UpdateProductNameUseCaseTest {

    @Mock
    private FranchiseGateway gateway;

    @InjectMocks
    private UpdateProductNameUseCase updateProductNameUseCase;

    private Franchise franchise;
    private String franchiseId;
    private String branchName;
    private String currentName;
    private String newName;

    @BeforeEach
    void setUp() {
        franchiseId = "franchise-1";
        branchName = "Sucursal Centro";
        currentName = "Producto Viejo";
        newName = "Producto Nuevo";

        franchise = Franchise.builder()
                .id(franchiseId)
                .name("Franquicia Test")
                .build();
    }

    @Test
    @DisplayName("Debe actualizar el nombre del producto exitosamente")
    void shouldUpdateProductNameSuccessfully() {
        // Arrange
        when(gateway.updateProductName(eq(franchiseId), eq(branchName), eq(currentName), eq(newName)))
                .thenReturn(Mono.just(franchise));

        // Act
        Mono<Franchise> result = updateProductNameUseCase.apply(franchiseId, branchName, currentName, newName);

        // Assert
        StepVerifier.create(result)
                .expectNext(franchise)
                .verifyComplete();

        verify(gateway, times(1)).updateProductName(franchiseId, branchName, currentName, newName);
    }

    @Test
    @DisplayName("Debe lanzar error cuando el nuevo nombre es nulo")
    void shouldThrowErrorWhenNewNameIsNull() {
        // Arrange
        String nullName = null;

        // Act
        Mono<Franchise> result = updateProductNameUseCase.apply(franchiseId, branchName, currentName, nullName);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("New name cannot be empty"))
                .verify();

        verify(gateway, never()).updateProductName(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar error cuando el nuevo nombre esta vacio")
    void shouldThrowErrorWhenNewNameIsEmpty() {
        // Arrange
        String emptyName = "";

        // Act
        Mono<Franchise> result = updateProductNameUseCase.apply(franchiseId, branchName, currentName, emptyName);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("New name cannot be empty"))
                .verify();

        verify(gateway, never()).updateProductName(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar error cuando el nuevo nombre solo contiene espacios")
    void shouldThrowErrorWhenNewNameIsBlank() {
        // Arrange
        String blankName = "   ";

        // Act
        Mono<Franchise> result = updateProductNameUseCase.apply(franchiseId, branchName, currentName, blankName);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("New name cannot be empty"))
                .verify();

        verify(gateway, never()).updateProductName(anyString(), anyString(), anyString(), anyString());
    }
}

