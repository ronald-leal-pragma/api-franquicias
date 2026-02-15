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
@DisplayName("Tests unitarios para UpdateBranchNameUseCase")
class UpdateBranchNameUseCaseTest {

    @Mock
    private FranchiseGateway gateway;

    @InjectMocks
    private UpdateBranchNameUseCase updateBranchNameUseCase;

    private Franchise franchise;
    private String franchiseId;
    private String currentName;
    private String newName;

    @BeforeEach
    void setUp() {
        franchiseId = "franchise-1";
        currentName = "Sucursal Vieja";
        newName = "Sucursal Nueva";

        franchise = Franchise.builder()
                .id(franchiseId)
                .name("Franquicia Test")
                .build();
    }

    @Test
    @DisplayName("Debe actualizar el nombre de la sucursal exitosamente")
    void shouldUpdateBranchNameSuccessfully() {
        // Arrange
        when(gateway.updateBranchName(eq(franchiseId), eq(currentName), eq(newName)))
                .thenReturn(Mono.just(franchise));

        // Act
        Mono<Franchise> result = updateBranchNameUseCase.apply(franchiseId, currentName, newName);

        // Assert
        StepVerifier.create(result)
                .expectNext(franchise)
                .verifyComplete();

        verify(gateway, times(1)).updateBranchName(franchiseId, currentName, newName);
    }

    @Test
    @DisplayName("Debe lanzar error cuando el nuevo nombre es nulo")
    void shouldThrowErrorWhenNewNameIsNull() {
        // Arrange
        String nullName = null;

        // Act
        Mono<Franchise> result = updateBranchNameUseCase.apply(franchiseId, currentName, nullName);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("New name cannot be empty"))
                .verify();

        verify(gateway, never()).updateBranchName(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar error cuando el nuevo nombre esta vacio")
    void shouldThrowErrorWhenNewNameIsEmpty() {
        // Arrange
        String emptyName = "";

        // Act
        Mono<Franchise> result = updateBranchNameUseCase.apply(franchiseId, currentName, emptyName);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("New name cannot be empty"))
                .verify();

        verify(gateway, never()).updateBranchName(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar error cuando el nuevo nombre solo contiene espacios")
    void shouldThrowErrorWhenNewNameIsBlank() {
        // Arrange
        String blankName = "   ";

        // Act
        Mono<Franchise> result = updateBranchNameUseCase.apply(franchiseId, currentName, blankName);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("New name cannot be empty"))
                .verify();

        verify(gateway, never()).updateBranchName(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Debe invocar el gateway con los parametros correctos")
    void shouldInvokeGatewayWithCorrectParameters() {
        // Arrange
        String customId = "custom-id";
        String customCurrentName = "Old Branch";
        String customNewName = "New Branch";

        when(gateway.updateBranchName(eq(customId), eq(customCurrentName), eq(customNewName)))
                .thenReturn(Mono.just(franchise));

        // Act
        updateBranchNameUseCase.apply(customId, customCurrentName, customNewName);

        // Assert
        verify(gateway, times(1)).updateBranchName(
                eq(customId),
                eq(customCurrentName),
                eq(customNewName)
        );
    }
}

