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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios para CreateFranchiseUseCase")
class CreateFranchiseUseCaseTest {

    @Mock
    private FranchiseGateway franchiseGateway;

    @InjectMocks
    private CreateFranchiseUseCase createFranchiseUseCase;

    private Franchise franchise;

    @BeforeEach
    void setUp() {
        franchise = Franchise.builder()
                .id("1")
                .name("Franquicia Test")
                .build();
    }

    @Test
    @DisplayName("Debe crear una franquicia exitosamente cuando el nombre es valido")
    void shouldCreateFranchiseSuccessfully() {
        // Arrange
        when(franchiseGateway.saveFranchise(any(Franchise.class)))
                .thenReturn(Mono.just(franchise));

        // Act
        Mono<Franchise> result = createFranchiseUseCase.apply(franchise);

        // Assert
        StepVerifier.create(result)
                .expectNext(franchise)
                .verifyComplete();

        verify(franchiseGateway, times(1)).saveFranchise(franchise);
    }

    @Test
    @DisplayName("Debe lanzar error cuando el nombre de la franquicia es nulo")
    void shouldThrowErrorWhenNameIsNull() {
        // Arrange
        franchise.setName(null);

        // Act
        Mono<Franchise> result = createFranchiseUseCase.apply(franchise);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Franchise name cannot be empty"))
                .verify();

        verify(franchiseGateway, never()).saveFranchise(any());
    }

    @Test
    @DisplayName("Debe lanzar error cuando el nombre de la franquicia esta vacio")
    void shouldThrowErrorWhenNameIsEmpty() {
        // Arrange
        franchise.setName("");

        // Act
        Mono<Franchise> result = createFranchiseUseCase.apply(franchise);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Franchise name cannot be empty"))
                .verify();

        verify(franchiseGateway, never()).saveFranchise(any());
    }

    @Test
    @DisplayName("Debe lanzar error cuando el nombre de la franquicia solo contiene espacios")
    void shouldThrowErrorWhenNameIsBlank() {
        // Arrange
        franchise.setName("   ");

        // Act
        Mono<Franchise> result = createFranchiseUseCase.apply(franchise);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Franchise name cannot be empty"))
                .verify();

        verify(franchiseGateway, never()).saveFranchise(any());
    }
}

