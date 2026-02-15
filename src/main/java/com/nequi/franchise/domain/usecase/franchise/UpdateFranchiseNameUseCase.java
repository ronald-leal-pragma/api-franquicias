package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateFranchiseNameUseCase {
    private final FranchiseGateway gateway;

    public Mono<Franchise> apply(String franchiseId, String newName) {
        if (newName == null || newName.isBlank()) {
            return Mono.error(new IllegalArgumentException("New name cannot be empty"));
        }
        return gateway.updateFranchiseName(franchiseId, newName);
    }
}