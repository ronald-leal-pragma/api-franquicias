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

    public Mono<Franchise> apply(String franchiseId, String branchName, String productName) {
        log.info("Method: RemoveProductUseCase.apply - Input: franchiseId={}, branchName={}, productName={}", franchiseId, branchName, productName);

        return gateway.removeProduct(franchiseId, branchName, productName)
                .doOnSuccess(updated -> log.info("Method: RemoveProductUseCase.apply - Output: franchiseId={}, branchName={}, productName={} removed", franchiseId, branchName, productName))
                .doOnError(error -> log.error("Method: RemoveProductUseCase.apply - Error: franchiseId={}, branchName={}, productName={}, message={}", franchiseId, branchName, productName, error.getMessage(), error));
    }
}
