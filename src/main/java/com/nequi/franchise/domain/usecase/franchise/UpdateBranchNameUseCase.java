package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateBranchNameUseCase {
    private final FranchiseGateway gateway;

    public Mono<Franchise> apply(String franchiseId, String currentName, String newName) {
        if (newName == null || newName.isBlank()) return Mono.error(new IllegalArgumentException("New name cannot be empty"));
        return gateway.updateBranchName(franchiseId, currentName, newName);
    }
}