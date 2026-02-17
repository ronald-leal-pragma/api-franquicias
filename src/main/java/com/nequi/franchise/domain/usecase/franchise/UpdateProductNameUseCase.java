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
public class UpdateProductNameUseCase {
    private final FranchiseGateway gateway;

    public Mono<Franchise> apply(String franchiseId, String branchName, String currentName, String newName) {
        log.info("Method: UpdateProductNameUseCase.apply - Input: franchiseId={}, branchName={}, currentName={}, newName={}", franchiseId, branchName, currentName, newName);

        return Optional.ofNullable(newName)
                .filter(name -> !name.isBlank())
                .map(name -> gateway.updateProductName(franchiseId, branchName, currentName, name)
                        .doOnSuccess(updated -> log.info("Method: UpdateProductNameUseCase.apply - Output: franchiseId={}, branchName={}, currentName={}, newName={}", franchiseId, branchName, currentName, newName))
                        .doOnError(error -> log.error("Method: UpdateProductNameUseCase.apply - Error: franchiseId={}, branchName={}, currentName={}, newName={}, message={}", franchiseId, branchName, currentName, newName, error.getMessage(), error))
                )
                .orElseGet(() -> Mono.error(new ValidationException("El nuevo nombre no puede estar vacío")));
    }
}

