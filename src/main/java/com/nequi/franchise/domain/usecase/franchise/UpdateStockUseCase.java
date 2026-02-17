package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.exception.ValidationException;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class UpdateStockUseCase {
    private final FranchiseGateway gateway;

    public Mono<Franchise> apply(String franchiseId, String branchName, String productName, Integer newStock) {
        log.info("Method: UpdateStockUseCase.apply - Input: franchiseId={}, branchName={}, productName={}, newStock={}", franchiseId, branchName, productName, newStock);

        return Optional.ofNullable(newStock)
                .filter(stock -> stock < 0)
                .map(stock -> Mono.<Franchise>error(new ValidationException("El stock no puede ser negativo")))
                .orElseGet(() -> gateway.updateStock(franchiseId, branchName, productName, newStock)
                        .doOnSuccess(updated -> log.info("Method: UpdateStockUseCase.apply - Output: franchiseId={}, branchName={}, productName={}, newStock={}", franchiseId, branchName, productName, newStock))
                        .doOnError(error -> log.error("Method: UpdateStockUseCase.apply - Error: franchiseId={}, branchName={}, productName={}, newStock={}, message={}", franchiseId, branchName, productName, newStock, error.getMessage(), error))
                );
    }
}
