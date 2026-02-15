package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.model.franchise.BranchProductResult;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class FindMaxStockUseCase {
    private final FranchiseGateway gateway;

    public Flux<BranchProductResult> apply(String franchiseId) {
        return gateway.findMaxStockByBranch(franchiseId);
    }
}
