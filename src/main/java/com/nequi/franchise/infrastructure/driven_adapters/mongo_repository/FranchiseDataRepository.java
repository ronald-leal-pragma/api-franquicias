package com.nequi.franchise.infrastructure.driven_adapters.mongo_repository;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface FranchiseDataRepository extends ReactiveMongoRepository<FranchiseDocument, String> {
    Mono<FranchiseDocument> findByName(String name);
}
