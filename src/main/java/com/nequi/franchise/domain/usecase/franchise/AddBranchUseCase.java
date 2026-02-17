package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.exception.BusinessException;
import com.nequi.franchise.domain.exception.ValidationException;
import com.nequi.franchise.domain.model.franchise.Branch;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class AddBranchUseCase {
    private final FranchiseGateway franchiseGateway;

    public Mono<Franchise> apply(String franchiseId, Branch branch) {
        return Mono.justOrEmpty(branch.getName())
                .doOnSubscribe(s -> log.info("Method: AddBranchUseCase.apply - Input: franchiseId={}, branch={}", franchiseId, branch))
                .filter(name -> !name.isBlank())
                .switchIfEmpty(Mono.error(new ValidationException("El nombre de la sucursal no puede estar vacío")))
                .flatMap(name -> franchiseGateway.findById(franchiseId))
                .flatMap(franchise -> franchise.getBranches().stream()
                        .filter(b -> b.getName().equalsIgnoreCase(branch.getName()))
                        .findFirst()
                        .map(existingBranch -> Mono.<Franchise>error(new BusinessException(
                                "Ya existe una sucursal con el nombre '" + branch.getName() + "' en esta franquicia"
                        )))
                        .orElseGet(() -> franchiseGateway.addBranch(franchiseId, branch))
                )
                .doOnSuccess(updated -> log.info("Method: AddBranchUseCase.apply - Output: franchiseId={}, branchName={}, totalBranches={}", franchiseId, branch.getName(), updated.getBranches().size()))
                .doOnError(error -> log.error("Method: AddBranchUseCase.apply - Error: franchiseId={}, branchName={}, message={}", franchiseId, branch.getName(), error.getMessage(), error));
    }
}
