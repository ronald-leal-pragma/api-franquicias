package com.nequi.franchise.infrastructure.driven_adapters.mongo_repository;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FranchiseDataRepository extends ReactiveMongoRepository<FranchiseDocument, String> {
}
