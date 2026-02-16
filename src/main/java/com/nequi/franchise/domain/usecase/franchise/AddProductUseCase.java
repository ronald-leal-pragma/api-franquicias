package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.exception.ValidationException;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.franchise.Product;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RequiredArgsConstructor
public class AddProductUseCase {
    private final FranchiseGateway gateway;

    public Mono<Franchise> apply(String franchiseId, String branchName, Product product) {
        return Optional.ofNullable(product.getStock())
                .filter(stock -> stock < 0)
                .map(stock -> Mono.<Franchise>error(new ValidationException("El stock no puede ser negativo")))
                .orElseGet(() -> gateway.addProduct(franchiseId, branchName, product));
    }
}
