package com.nequi.franchise.infrastructure.driven_adapters.mongo_repository;

import com.nequi.franchise.domain.exception.ResourceNotFoundException;
import com.nequi.franchise.domain.model.franchise.Branch;
import com.nequi.franchise.domain.model.franchise.BranchProductResult;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.franchise.Product;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Comparator;

/**
 * Adaptador del repositorio de franquicias con Circuit Breaker usando anotaciones.
 * Implementación con @CircuitBreaker de forma declarativa para protección contra fallos.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class FranchiseRepositoryAdapter implements FranchiseGateway {
    private static final String FRANQUICIA_NO_ENCONTRADA = "Franquicia o sucursal no encontrada";
    private static final String BRANCHES_NAME = "branches.name";
    private static final String SERVICE_OPERATION_MONGODB = "mongodb";

    private final FranchiseDataRepository repository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final FranchiseMapper mapper;

    @Override
    @CircuitBreaker(name = SERVICE_OPERATION_MONGODB)
    public Mono<Franchise> saveFranchise(Franchise franchise) {
        log.debug("Guardando franquicia: {}", franchise.getName());
        return Mono.just(franchise)
                .map(mapper::toDocument)
                .flatMap(repository::save)
                .map(mapper::toEntity);
    }

    @Override
    @CircuitBreaker(name = SERVICE_OPERATION_MONGODB)
    public Mono<Franchise> findByName(String name) {
        log.debug("Buscando franquicia por nombre: {}", name);
        return repository.findByName(name)
                .map(mapper::toEntity);
    }

    @Override
    @CircuitBreaker(name = SERVICE_OPERATION_MONGODB)
    public Mono<Franchise> findById(String id) {
        log.debug("Buscando franquicia por ID: {}", id);
        return repository.findById(id)
                .map(mapper::toEntity);
    }

    @Override
    @CircuitBreaker(name = SERVICE_OPERATION_MONGODB)
    public Mono<Franchise> addBranch(String franchiseId, Branch branch) {
        log.debug("Agregando sucursal '{}' a franquicia ID: {}", branch.getName(), franchiseId);
        Query query = Query.query(Criteria.where("id").is(franchiseId));
        Update update = new Update().push("branches", mapper.toBranchDocument(branch));

        return mongoTemplate.findAndModify(query, update,
                        new org.springframework.data.mongodb.core.FindAndModifyOptions().returnNew(true),
                        FranchiseDocument.class)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada con ID: " + franchiseId)))
                .map(mapper::toEntity);
    }

    @Override
    @CircuitBreaker(name = SERVICE_OPERATION_MONGODB)
    public Mono<Franchise> addProduct(String franchiseId, String branchName, Product product) {
        log.debug("Agregando producto '{}' a sucursal '{}' en franquicia ID: {}",
                product.getName(), branchName, franchiseId);
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
    @CircuitBreaker(name = SERVICE_OPERATION_MONGODB)
    public Mono<Franchise> removeProduct(String franchiseId, String branchName, String productName) {
        log.debug("Eliminando producto '{}' de sucursal '{}' en franquicia ID: {}",
                productName, branchName, franchiseId);
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
    @CircuitBreaker(name = SERVICE_OPERATION_MONGODB)
    public Mono<Franchise> updateStock(String franchiseId, String branchName, String productName, Integer newStock) {
        log.debug("Actualizando stock de producto '{}' a {} en sucursal '{}', franquicia ID: {}",
                productName, newStock, branchName, franchiseId);
        Query query = Query.query(Criteria.where("id").is(franchiseId)
                .and(BRANCHES_NAME).is(branchName)
                .and("branches.products.name").is(productName));

        Update update = new Update().set("branches.$[b].products.$[p].stock", newStock);

        return getFranchiseMono(branchName, productName, query, update);
    }

    @Override
    @CircuitBreaker(name = SERVICE_OPERATION_MONGODB)
    public Flux<BranchProductResult> findMaxStockByBranch(String franchiseId) {
        log.debug("Buscando productos con mayor stock por sucursal en franquicia ID: {}", franchiseId);
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
    @CircuitBreaker(name = SERVICE_OPERATION_MONGODB)
    public Mono<Franchise> updateFranchiseName(String franchiseId, String newName) {
        log.debug("Actualizando nombre de franquicia ID: {} a '{}'", franchiseId, newName);
        Query query = Query.query(Criteria.where("id").is(franchiseId));
        Update update = new Update().set("name", newName);

        return mongoTemplate.findAndModify(query, update,
                        new org.springframework.data.mongodb.core.FindAndModifyOptions().returnNew(true),
                        FranchiseDocument.class)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada con ID: " + franchiseId)))
                .map(mapper::toEntity);
    }

    @Override
    @CircuitBreaker(name = SERVICE_OPERATION_MONGODB)
    public Mono<Franchise> updateBranchName(String franchiseId, String currentName, String newName) {
        log.debug("Actualizando nombre de sucursal '{}' a '{}' en franquicia ID: {}",
                currentName, newName, franchiseId);
        Query query = Query.query(Criteria.where("id").is(franchiseId).and(BRANCHES_NAME).is(currentName));
        Update update = new Update().set("branches.$.name", newName);

        return mongoTemplate.findAndModify(query, update,
                        new org.springframework.data.mongodb.core.FindAndModifyOptions().returnNew(true),
                        FranchiseDocument.class)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(FRANQUICIA_NO_ENCONTRADA)))
                .map(mapper::toEntity);
    }

    @Override
    @CircuitBreaker(name = SERVICE_OPERATION_MONGODB)
    public Mono<Franchise> updateProductName(String franchiseId, String branchName, String currentName, String newName) {
        log.debug("Actualizando nombre de producto '{}' a '{}' en sucursal '{}', franquicia ID: {}",
                currentName, newName, branchName, franchiseId);
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

