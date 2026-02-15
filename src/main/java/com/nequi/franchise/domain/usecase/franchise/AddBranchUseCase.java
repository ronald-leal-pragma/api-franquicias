package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.exception.BusinessException;
import com.nequi.franchise.domain.exception.ValidationException;
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
            return Mono.error(new ValidationException("El nombre de la sucursal no puede estar vacío"));
        }

        return gateway.findById(franchiseId)
                .flatMap(franchise -> {
                    boolean branchExists = franchise.getBranches().stream()
                            .anyMatch(b -> b.getName().equalsIgnoreCase(branch.getName()));

                    if (branchExists) {
                        return Mono.error(new BusinessException(
                            "Ya existe una sucursal con el nombre '" + branch.getName() + "' en esta franquicia"
                        ));
                    }

                    return gateway.addBranch(franchiseId, branch);
                });
    }
}
