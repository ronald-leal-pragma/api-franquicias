package com.nequi.franchise.infrastructure.driven_adapters.mongo_repository;

import com.nequi.franchise.domain.exception.ResourceNotFoundException;
import com.nequi.franchise.domain.model.franchise.Branch;
import com.nequi.franchise.domain.model.franchise.BranchProductResult;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.franchise.Product;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Comparator;

@Repository
@RequiredArgsConstructor
public class FranchiseRepositoryAdapter implements FranchiseGateway {
    private static final String FRANQUICIA_NO_ENCONTRADA = "Franquicia o sucursal no encontrada";
    private static final String BRANCHES_NAME = "branches.name";
    private final FranchiseDataRepository repository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final FranchiseMapper mapper;

    @Override
    public Mono<Franchise> saveFranchise(Franchise franchise) {
        return Mono.just(franchise)
                .map(mapper::toDocument)
                .flatMap(repository::save)
                .map(mapper::toEntity);
    }

    @Override
    public Mono<Franchise> findByName(String name) {
        return repository.findByName(name)
                .map(mapper::toEntity);
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return repository.findById(id)
                .map(mapper::toEntity);
    }

    @Override
    public Mono<Franchise> addBranch(String franchiseId, Branch branch) {
        Query query = Query.query(Criteria.where("id").is(franchiseId));

        Update update = new Update().push("branches", mapper.toBranchDocument(branch));

        return mongoTemplate.findAndModify(query, update,
                        new org.springframework.data.mongodb.core.FindAndModifyOptions().returnNew(true),
                        FranchiseDocument.class)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada con ID: " + franchiseId)))
                .map(mapper::toEntity);
    }

    @Override
    public Mono<Franchise> addProduct(String franchiseId, String branchName, Product product) {
        Query query = Query.query(Criteria.where("id").is(franchiseId)
                .and(BRANCHES_NAME).is(branchName));

        Update update = new Update().push("branches.$[elem].products", mapper.toProductDocument(product));

        update.filterArray(Criteria.where("elem.name").is(branchName));

        return mongoTemplate.findAndModify(query, update,
                        new org.springframework.data.mongodb.core.FindAndModifyOptions().returnNew(true),
                        FranchiseDocument.class)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(FRANQUICIA_NO_ENCONTRADA)))
                .map(mapper::toEntity);
    }

    @Override
    public Mono<Franchise> removeProduct(String franchiseId, String branchName, String productName) {
        Query query = Query.query(Criteria.where("id").is(franchiseId)
                .and(BRANCHES_NAME).is(branchName));

        Update update = new Update().pull("branches.$.products", Collections.singletonMap("name", productName));

        return mongoTemplate.findAndModify(query, update,
                        new org.springframework.data.mongodb.core.FindAndModifyOptions().returnNew(true),
                        FranchiseDocument.class)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(FRANQUICIA_NO_ENCONTRADA)))
                .map(mapper::toEntity);
    }

    @Override
    public Mono<Franchise> updateStock(String franchiseId, String branchName, String productName, Integer newStock) {
        Query query = Query.query(Criteria.where("id").is(franchiseId)
                .and(BRANCHES_NAME).is(branchName)
                .and("branches.products.name").is(productName));

        Update update = new Update().set("branches.$[b].products.$[p].stock", newStock);

        return getFranchiseMono(branchName, productName, query, update);
    }

    @Override
    public Flux<BranchProductResult> findMaxStockByBranch(String franchiseId) {
        return repository.findById(franchiseId)
                .map(mapper::toEntity)
                .flatMapMany(franchise -> Flux.fromIterable(franchise.getBranches()))
                .map(branch -> {
                    Product maxProduct = branch.getProducts().stream()
                            .max(Comparator.comparingInt(Product::getStock))
                            .orElse(null);

                    return new BranchProductResult(branch.getName(), maxProduct);
                })
                .filter(result -> result.getProduct() != null);
    }

    @Override
    public Mono<Franchise> updateFranchiseName(String franchiseId, String newName) {
        Query query = Query.query(Criteria.where("id").is(franchiseId));
        Update update = new Update().set("name", newName);

        return mongoTemplate.findAndModify(query, update,
                        new org.springframework.data.mongodb.core.FindAndModifyOptions().returnNew(true),
                        FranchiseDocument.class)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada con ID: " + franchiseId)))
                .map(mapper::toEntity);
    }

    @Override
    public Mono<Franchise> updateBranchName(String franchiseId, String currentName, String newName) {
        Query query = Query.query(Criteria.where("id").is(franchiseId).and(BRANCHES_NAME).is(currentName));
        Update update = new Update().set("branches.$.name", newName);

        return mongoTemplate.findAndModify(query, update,
                        new org.springframework.data.mongodb.core.FindAndModifyOptions().returnNew(true),
                        FranchiseDocument.class)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(FRANQUICIA_NO_ENCONTRADA)))
                .map(mapper::toEntity);
    }

    @Override
    public Mono<Franchise> updateProductName(String franchiseId, String branchName, String currentName, String newName) {
        Query query = Query.query(Criteria.where("id").is(franchiseId)
                .and(BRANCHES_NAME).is(branchName)
                .and("branches.products.name").is(currentName));

        Update update = new Update().set("branches.$[b].products.$[p].name", newName);

        return getFranchiseMono(branchName, currentName, query, update);
    }

    @NonNull
    private Mono<Franchise> getFranchiseMono(String branchName, String currentName, Query query, Update update) {
        update.filterArray(Criteria.where("b.name").is(branchName));
        update.filterArray(Criteria.where("p.name").is(currentName));

        return mongoTemplate.findAndModify(query, update,
                        new org.springframework.data.mongodb.core.FindAndModifyOptions().returnNew(true),
                        FranchiseDocument.class)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Producto no encontrado")))
                .map(mapper::toEntity);
    }
}
