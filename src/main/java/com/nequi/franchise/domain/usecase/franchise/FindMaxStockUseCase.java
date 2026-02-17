package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.model.franchise.BranchProductResult;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
public class FindMaxStockUseCase {
    private final FranchiseGateway gateway;

    public Flux<BranchProductResult> apply(String franchiseId) {
        return gateway.findMaxStockByBranch(franchiseId)
                .doOnSubscribe(s -> log.info("Method: FindMaxStockUseCase.apply - Input: franchiseId={}", franchiseId))
                .doOnNext(result -> log.info("Method: FindMaxStockUseCase.apply - Output item: franchiseId={}, branchName={}, productName={}, stock={}",
                        franchiseId, result.getBranchName(), result.getProduct() != null ? result.getProduct().getName() : "N/A",
                        result.getProduct() != null ? result.getProduct().getStock() : 0))
                .doOnComplete(() -> log.info("Method: FindMaxStockUseCase.apply - Output: franchiseId={}, completed", franchiseId))
                .doOnError(error -> log.error("Method: FindMaxStockUseCase.apply - Error: franchiseId={}, message={}", franchiseId, error.getMessage(), error));
    }
}
