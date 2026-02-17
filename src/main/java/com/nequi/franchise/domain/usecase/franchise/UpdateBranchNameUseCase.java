package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.exception.ValidationException;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class UpdateBranchNameUseCase {
    private final FranchiseGateway gateway;

    public Mono<Franchise> apply(String franchiseId, String branchId, String newName) {
        log.info("Method: UpdateBranchNameUseCase.apply - Input: franchiseId={}, branchId={}, newName={}", franchiseId, branchId, newName);

        return Optional.ofNullable(newName)
                .filter(name -> !name.isBlank())
                .map(name -> gateway.updateBranchName(franchiseId, branchId, name)
                        .doOnSuccess(updated -> log.info("Method: UpdateBranchNameUseCase.apply - Output: franchiseId={}, branchId={}, newName={}", franchiseId, branchId, newName))
                        .doOnError(error -> log.error("Method: UpdateBranchNameUseCase.apply - Error: franchiseId={}, branchId={}, newName={}, message={}", franchiseId, branchId, newName, error.getMessage(), error))
                )
                .orElseGet(() -> Mono.error(new ValidationException("El nuevo nombre no puede estar vacío")));
    }
}
