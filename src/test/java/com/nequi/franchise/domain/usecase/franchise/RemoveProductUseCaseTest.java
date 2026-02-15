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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios para RemoveProductUseCase")
class RemoveProductUseCaseTest {

    @Mock
    private FranchiseGateway gateway;

    @InjectMocks
    private RemoveProductUseCase removeProductUseCase;

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
    @DisplayName("Debe eliminar un producto exitosamente")
    void shouldRemoveProductSuccessfully() {
        // Arrange
        when(gateway.removeProduct(eq(franchiseId), eq(branchName), eq(productName)))
                .thenReturn(Mono.just(franchise));

        // Act
        Mono<Franchise> result = removeProductUseCase.apply(franchiseId, branchName, productName);

        // Assert
        StepVerifier.create(result)
                .expectNext(franchise)
                .verifyComplete();

        verify(gateway, times(1)).removeProduct(franchiseId, branchName, productName);
    }

    @Test
    @DisplayName("Debe manejar error cuando el gateway falla")
    void shouldHandleGatewayError() {
        // Arrange
        when(gateway.removeProduct(eq(franchiseId), eq(branchName), eq(productName)))
                .thenReturn(Mono.error(new RuntimeException("Error de base de datos")));

        // Act
        Mono<Franchise> result = removeProductUseCase.apply(franchiseId, branchName, productName);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Error de base de datos"))
                .verify();

        verify(gateway, times(1)).removeProduct(franchiseId, branchName, productName);
    }

    @Test
    @DisplayName("Debe invocar el gateway con los parametros correctos")
    void shouldInvokeGatewayWithCorrectParameters() {
        // Arrange
        String customFranchiseId = "custom-id";
        String customBranchName = "Custom Branch";
        String customProductName = "Custom Product";

        when(gateway.removeProduct(eq(customFranchiseId), eq(customBranchName), eq(customProductName)))
                .thenReturn(Mono.just(franchise));

        // Act
        removeProductUseCase.apply(customFranchiseId, customBranchName, customProductName);

        // Assert
        verify(gateway, times(1)).removeProduct(
                eq(customFranchiseId),
                eq(customBranchName),
                eq(customProductName)
        );
    }
}

