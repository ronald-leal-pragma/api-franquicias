package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.exception.ValidationException;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.franchise.Product;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class AddProductUseCase {
    private final FranchiseGateway gateway;

    public Mono<Franchise> apply(String franchiseId, String branchName, Product product) {
        log.info("Method: AddProductUseCase.apply - Input: franchiseId={}, branchName={}, product={}", franchiseId, branchName, product);

        return Optional.ofNullable(product.getStock())
                .filter(stock -> stock < 0)
                .map(stock -> Mono.<Franchise>error(new ValidationException("El stock no puede ser negativo")))
                .orElseGet(() -> gateway.addProduct(franchiseId, branchName, product)
                        .doOnSuccess(updated -> log.info("Method: AddProductUseCase.apply - Output: franchiseId={}, branchName={}, productName={}", franchiseId, branchName, product.getName()))
                        .doOnError(error -> log.error("Method: AddProductUseCase.apply - Error: franchiseId={}, branchName={}, productName={}, message={}", franchiseId, branchName, product.getName(), error.getMessage(), error))
                );
    }
}
