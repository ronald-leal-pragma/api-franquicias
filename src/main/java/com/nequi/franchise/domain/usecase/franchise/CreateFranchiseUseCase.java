package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.exception.BusinessException;
import com.nequi.franchise.domain.exception.ValidationException;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateFranchiseUseCase {
    private final FranchiseGateway franchiseGateway;

    public Mono<Franchise> apply(Franchise franchise) {
        if (franchise.getName() == null || franchise.getName().trim().isEmpty()) {
            return Mono.error(new ValidationException("El nombre de la franquicia no puede estar vacío"));
        }

        return franchiseGateway.findByName(franchise.getName())
                .flatMap(existingFranchise ->
                        Mono.<Franchise>error(new BusinessException(
                                "Ya existe una franquicia con el nombre: " + franchise.getName()
                        ))
                )
                .switchIfEmpty(franchiseGateway.saveFranchise(franchise));
    }
}
