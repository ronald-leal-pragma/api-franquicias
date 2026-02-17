package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.exception.ValidationException;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.franchise.Product;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import com.nequi.franchise.domain.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class AddProductUseCase {
    private final FranchiseGateway gateway;

    public Mono<Franchise> apply(String franchiseId, String branchId, Product product) {
        log.info("Method: AddProductUseCase.apply - Input: franchiseId={}, branchId={}, product={}", franchiseId, branchId, product);

        return Optional.ofNullable(product.getStock())
                .filter(stock -> stock < 0)
                .map(stock -> Mono.<Franchise>error(new ValidationException("El stock no puede ser negativo")))
                .orElseGet(() -> {
                    // Generar productId si no existe
                    Optional.ofNullable(product.getProductId())
                            .filter(id -> !id.isEmpty())
                            .ifPresentOrElse(
                                    id -> {},
                                    () -> product.setProductId(IdGenerator.generateId())
                            );

                    return gateway.addProduct(franchiseId, branchId, product)
                            .doOnSuccess(updated -> log.info("Method: AddProductUseCase.apply - Output: franchiseId={}, branchId={}, productId={}, productName={}",
                                    franchiseId, branchId, product.getProductId(), product.getName()))
                            .doOnError(error -> log.error("Method: AddProductUseCase.apply - Error: franchiseId={}, branchId={}, productName={}, message={}",
                                    franchiseId, branchId, product.getName(), error.getMessage(), error));
                });
    }
}
