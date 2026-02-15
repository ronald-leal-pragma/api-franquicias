package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateStockUseCase {
    private final FranchiseGateway gateway;

    public Mono<Franchise> apply(String franchiseId, String branchName, String productName, Integer newStock) {
        if (newStock < 0) {
            return Mono.error(new IllegalArgumentException("Stock cannot be negative"));
        }
        return gateway.updateStock(franchiseId, branchName, productName, newStock);
    }
}
