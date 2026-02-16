package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.exception.BusinessException;
import com.nequi.franchise.domain.exception.ValidationException;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RequiredArgsConstructor
public class CreateFranchiseUseCase {
    private final FranchiseGateway franchiseGateway;

    public Mono<Franchise> apply(Franchise franchise) {
        return Optional.ofNullable(franchise.getName())
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .map(name -> franchiseGateway.findByName(name)
                        .flatMap(existingFranchise ->
                                Mono.<Franchise>error(new BusinessException(
                                        "Ya existe una franquicia con el nombre: " + name
                                ))
                        )
                        .switchIfEmpty(franchiseGateway.saveFranchise(franchise))
                )
                .orElseGet(() -> Mono.error(new ValidationException("El nombre de la franquicia no puede estar vacío")));
    }
}
