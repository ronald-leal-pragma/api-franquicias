package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.model.franchise.BranchProductResult;
import com.nequi.franchise.domain.model.franchise.Product;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios para FindMaxStockUseCase")
class FindMaxStockUseCaseTest {

    @Mock
    private FranchiseGateway gateway;

    @InjectMocks
    private FindMaxStockUseCase findMaxStockUseCase;

    private String franchiseId;
    private BranchProductResult result1;
    private BranchProductResult result2;
    private BranchProductResult result3;

    @BeforeEach
    void setUp() {
        franchiseId = "franchise-1";

        result1 = BranchProductResult.builder()
                .branchName("Sucursal Centro")
                .product(Product.builder().name("Producto A").stock(100).build())
                .build();

        result2 = BranchProductResult.builder()
                .branchName("Sucursal Norte")
                .product(Product.builder().name("Producto B").stock(200).build())
                .build();

        result3 = BranchProductResult.builder()
                .branchName("Sucursal Sur")
                .product(Product.builder().name("Producto C").stock(150).build())
                .build();
    }

    @Test
    @DisplayName("Debe retornar productos con mayor stock por sucursal exitosamente")
    void shouldFindMaxStockSuccessfully() {
        // Arrange
        when(gateway.findMaxStockByBranch(eq(franchiseId)))
                .thenReturn(Flux.just(result1, result2, result3));

        // Act
        Flux<BranchProductResult> result = findMaxStockUseCase.apply(franchiseId);

        // Assert
        StepVerifier.create(result)
                .expectNext(result1)
                .expectNext(result2)
                .expectNext(result3)
                .verifyComplete();

        verify(gateway, times(1)).findMaxStockByBranch(franchiseId);
    }

    @Test
    @DisplayName("Debe retornar Flux vacio cuando no hay resultados")
    void shouldReturnEmptyFluxWhenNoResults() {
        // Arrange
        when(gateway.findMaxStockByBranch(eq(franchiseId)))
                .thenReturn(Flux.empty());

        // Act
        Flux<BranchProductResult> result = findMaxStockUseCase.apply(franchiseId);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(gateway, times(1)).findMaxStockByBranch(franchiseId);
    }

    @Test
    @DisplayName("Debe retornar un solo resultado cuando solo hay una sucursal")
    void shouldReturnSingleResultWhenOnlyOneBranch() {
        // Arrange
        when(gateway.findMaxStockByBranch(eq(franchiseId)))
                .thenReturn(Flux.just(result1));

        // Act
        Flux<BranchProductResult> result = findMaxStockUseCase.apply(franchiseId);

        // Assert
        StepVerifier.create(result)
                .expectNext(result1)
                .verifyComplete();

        verify(gateway, times(1)).findMaxStockByBranch(franchiseId);
    }

    @Test
    @DisplayName("Debe manejar error cuando el gateway falla")
    void shouldHandleGatewayError() {
        // Arrange
        when(gateway.findMaxStockByBranch(eq(franchiseId)))
                .thenReturn(Flux.error(new RuntimeException("Error de base de datos")));

        // Act
        Flux<BranchProductResult> result = findMaxStockUseCase.apply(franchiseId);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Error de base de datos"))
                .verify();

        verify(gateway, times(1)).findMaxStockByBranch(franchiseId);
    }

    @Test
    @DisplayName("Debe invocar el gateway con el ID de franquicia correcto")
    void shouldInvokeGatewayWithCorrectFranchiseId() {
        // Arrange
        String customFranchiseId = "custom-franchise-123";
        when(gateway.findMaxStockByBranch(eq(customFranchiseId)))
                .thenReturn(Flux.just(result1));

        // Act
        findMaxStockUseCase.apply(customFranchiseId);

        // Assert
        verify(gateway, times(1)).findMaxStockByBranch(eq(customFranchiseId));
    }
}

