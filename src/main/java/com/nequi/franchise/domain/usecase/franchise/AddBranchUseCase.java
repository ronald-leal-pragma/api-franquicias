package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.model.franchise.Branch;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AddBranchUseCase {
    private final FranchiseGateway gateway;

    public Mono<Franchise> apply(String franchiseId, Branch branch) {
        if (branch.getName() == null || branch.getName().isBlank()) {
            return Mono.error(new IllegalArgumentException("Branch name cannot be empty"));
        }

        return gateway.addBranch(franchiseId, branch);
    }
}
