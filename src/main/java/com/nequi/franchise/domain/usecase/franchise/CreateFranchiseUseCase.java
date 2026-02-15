package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateFranchiseUseCase {
    private final FranchiseGateway franchiseGateway;

    public Mono<Franchise> apply(Franchise franchise) {
        if (franchise.getName() == null || franchise.getName().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Franchise name cannot be empty"));
        }

        return franchiseGateway.saveFranchise(franchise);
    }
}
