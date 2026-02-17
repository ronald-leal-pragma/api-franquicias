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
public class UpdateFranchiseNameUseCase {
    private final FranchiseGateway gateway;

    public Mono<Franchise> apply(String franchiseId, String newName) {
        log.info("Method: UpdateFranchiseNameUseCase.apply - Input: franchiseId={}, newName={}", franchiseId, newName);

        return Optional.ofNullable(newName)
                .filter(name -> !name.isBlank())
                .map(name -> gateway.updateFranchiseName(franchiseId, name)
                        .doOnSuccess(updated -> log.info("Method: UpdateFranchiseNameUseCase.apply - Output: franchiseId={}, newName={}", franchiseId, newName))
                        .doOnError(error -> log.error("Method: UpdateFranchiseNameUseCase.apply - Error: franchiseId={}, newName={}, message={}", franchiseId, newName, error.getMessage(), error))
                )
                .orElseGet(() -> Mono.error(new ValidationException("El nuevo nombre no puede estar vacío")));
    }
}

