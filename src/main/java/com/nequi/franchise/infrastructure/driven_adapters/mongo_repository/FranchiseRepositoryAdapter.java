package com.nequi.franchise.infrastructure.driven_adapters.mongo_repository;

import com.nequi.franchise.domain.model.franchise.Branch;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.franchise.Product;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class FranchiseRepositoryAdapter implements FranchiseGateway {
    private final FranchiseDataRepository repository;
    private final FranchiseMapper mapper;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return Mono.just(franchise)
                .map(mapper::toDocument)
                .flatMap(repository::save)
                .map(mapper::toEntity);
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return repository.findById(id)
                .map(mapper::toEntity);
    }

    @Override
    public Mono<Void> updateStock(String franchiseId, String branchName, String productName, Integer stock) {
        return Mono.empty(); // TODO: Implementar con ReactiveMongoTemplate
    }

    @Override
    public Mono<Void> addBranch(String franchiseId, Branch branch) {
        return Mono.empty(); // TODO: Implementar con $push
    }

    @Override
    public Mono<Void> addProduct(String franchiseId, String branchName, Product product) {
        return Mono.empty(); // TODO: Implementar con arrayFilters
    }
}
