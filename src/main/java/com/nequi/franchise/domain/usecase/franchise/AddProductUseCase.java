package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.franchise.Product;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AddProductUseCase {
    private final FranchiseGateway gateway;

    public Mono<Franchise> apply(String franchiseId, String branchName, Product product) {
        if (product.getStock() != null && product.getStock() < 0) {
            return Mono.error(new IllegalArgumentException("Stock cannot be negative"));
        }
        return gateway.addProduct(franchiseId, branchName, product);
    }
}
