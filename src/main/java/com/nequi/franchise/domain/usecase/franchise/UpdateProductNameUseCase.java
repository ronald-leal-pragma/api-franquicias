package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.exception.ValidationException;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateProductNameUseCase {
    private final FranchiseGateway gateway;

    public Mono<Franchise> apply(String franchiseId, String branchName, String currentName, String newName) {
        if (newName == null || newName.isBlank()) return Mono.error(new ValidationException("El nuevo nombre no puede estar vacío"));
        return gateway.updateProductName(franchiseId, branchName, currentName, newName);
    }
}