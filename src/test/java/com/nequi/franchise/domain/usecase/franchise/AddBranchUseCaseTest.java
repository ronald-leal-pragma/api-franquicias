package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.exception.ValidationException;
import com.nequi.franchise.domain.model.franchise.Branch;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios para AddBranchUseCase")
class AddBranchUseCaseTest {

    @Mock
    private FranchiseGateway gateway;

    @InjectMocks
    private AddBranchUseCase addBranchUseCase;

    private Branch branch;
    private Franchise franchise;
    private String franchiseId;

    @BeforeEach
    void setUp() {
        franchiseId = "franchise-1";
        branch = Branch.builder()
                .name("Sucursal Centro")
                .build();

        franchise = Franchise.builder()
                .id(franchiseId)
                .name("Franquicia Test")
                .build();
    }

    @Test
    @DisplayName("Debe agregar una sucursal exitosamente cuando el nombre es valido")
    void shouldAddBranchSuccessfully() {
        // Arrange
        when(gateway.addBranch(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.just(franchise));

        // Act
        Mono<Franchise> result = addBranchUseCase.apply(franchiseId, branch);

        // Assert
        StepVerifier.create(result)
                .expectNext(franchise)
                .verifyComplete();

        verify(gateway, times(1)).addBranch(franchiseId, branch);
    }

    @Test
    @DisplayName("Debe lanzar error cuando el nombre de la sucursal es nulo")
    void shouldThrowErrorWhenBranchNameIsNull() {
        // Arrange
        branch.setName(null);

        // Act
        Mono<Franchise> result = addBranchUseCase.apply(franchiseId, branch);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().equals("El nombre de la sucursal no puede estar vacío"))
                .verify();

        verify(gateway, never()).addBranch(any(), any());
    }

    @Test
    @DisplayName("Debe lanzar error cuando el nombre de la sucursal esta vacio")
    void shouldThrowErrorWhenBranchNameIsEmpty() {
        // Arrange
        branch.setName("");

        // Act
        Mono<Franchise> result = addBranchUseCase.apply(franchiseId, branch);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().equals("El nombre de la sucursal no puede estar vacío"))
                .verify();

        verify(gateway, never()).addBranch(any(), any());
    }

    @Test
    @DisplayName("Debe lanzar error cuando el nombre de la sucursal solo contiene espacios")
    void shouldThrowErrorWhenBranchNameIsBlank() {
        // Arrange
        branch.setName("   ");

        // Act
        Mono<Franchise> result = addBranchUseCase.apply(franchiseId, branch);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().equals("El nombre de la sucursal no puede estar vacío"))
                .verify();

        verify(gateway, never()).addBranch(any(), any());
    }
}

