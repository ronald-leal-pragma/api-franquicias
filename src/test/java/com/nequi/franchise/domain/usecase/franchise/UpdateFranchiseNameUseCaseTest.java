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
@DisplayName("Tests unitarios para UpdateFranchiseNameUseCase")
class UpdateFranchiseNameUseCaseTest {

    @Mock
    private FranchiseGateway gateway;

    @InjectMocks
    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;

    private Franchise franchise;
    private String franchiseId;
    private String newName;

    @BeforeEach
    void setUp() {
        franchiseId = "franchise-1";
        newName = "Nuevo Nombre de Franquicia";

        franchise = Franchise.builder()
                .id(franchiseId)
                .name(newName)
                .build();
    }

    @Test
    @DisplayName("Debe actualizar el nombre de la franquicia exitosamente")
    void shouldUpdateFranchiseNameSuccessfully() {
        // Arrange
        when(gateway.updateFranchiseName(eq(franchiseId), eq(newName)))
                .thenReturn(Mono.just(franchise));

        // Act
        Mono<Franchise> result = updateFranchiseNameUseCase.apply(franchiseId, newName);

        // Assert
        StepVerifier.create(result)
                .expectNext(franchise)
                .verifyComplete();

        verify(gateway, times(1)).updateFranchiseName(franchiseId, newName);
    }

    @Test
    @DisplayName("Debe lanzar error cuando el nuevo nombre es nulo")
    void shouldThrowErrorWhenNewNameIsNull() {
        // Arrange
        String nullName = null;

        // Act
        Mono<Franchise> result = updateFranchiseNameUseCase.apply(franchiseId, nullName);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("New name cannot be empty"))
                .verify();

        verify(gateway, never()).updateFranchiseName(anyString(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar error cuando el nuevo nombre esta vacio")
    void shouldThrowErrorWhenNewNameIsEmpty() {
        // Arrange
        String emptyName = "";

        // Act
        Mono<Franchise> result = updateFranchiseNameUseCase.apply(franchiseId, emptyName);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("New name cannot be empty"))
                .verify();

        verify(gateway, never()).updateFranchiseName(anyString(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar error cuando el nuevo nombre solo contiene espacios")
    void shouldThrowErrorWhenNewNameIsBlank() {
        // Arrange
        String blankName = "   ";

        // Act
        Mono<Franchise> result = updateFranchiseNameUseCase.apply(franchiseId, blankName);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("New name cannot be empty"))
                .verify();

        verify(gateway, never()).updateFranchiseName(anyString(), anyString());
    }

    @Test
    @DisplayName("Debe invocar el gateway con los parametros correctos")
    void shouldInvokeGatewayWithCorrectParameters() {
        // Arrange
        String customId = "custom-id";
        String customName = "Custom Name";

        when(gateway.updateFranchiseName(eq(customId), eq(customName)))
                .thenReturn(Mono.just(franchise));

        // Act
        updateFranchiseNameUseCase.apply(customId, customName);

        // Assert
        verify(gateway, times(1)).updateFranchiseName(eq(customId), eq(customName));
    }
}

