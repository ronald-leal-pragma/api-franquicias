package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class RemoveProductUseCase {
    private final FranchiseGateway gateway;

    public Mono<Franchise> apply(String franchiseId, String branchId, String productId) {
        log.info("Method: RemoveProductUseCase.apply - Input: franchiseId={}, branchId={}, productId={}", franchiseId, branchId, productId);

        return gateway.removeProduct(franchiseId, branchId, productId)
                .doOnSuccess(updated -> log.info("Method: RemoveProductUseCase.apply - Output: franchiseId={}, branchId={}, productId={} removed", franchiseId, branchId, productId))
                .doOnError(error -> log.error("Method: RemoveProductUseCase.apply - Error: franchiseId={}, branchId={}, productId={}, message={}", franchiseId, branchId, productId, error.getMessage(), error));
    }
}
