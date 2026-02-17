package com.nequi.franchise.infrastructure.driven_adapters.mongo_repository;

import com.nequi.franchise.domain.exception.ResourceNotFoundException;
import com.nequi.franchise.domain.model.franchise.Branch;
import com.nequi.franchise.domain.model.franchise.BranchProductResult;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.franchise.Product;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

/**
 * Adaptador agnóstico a la tecnología de persistencia.
 * Usa branchId y productId para operaciones, permitiendo fácil migración a bases relacionales.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class FranchiseRepositoryAdapter implements FranchiseGateway {
    private static final String FRANQUICIA_NO_ENCONTRADA = "Franquicia o sucursal no encontrada";
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
        log.debug("Agregando sucursal branchId='{}' nombre='{}' a franquicia ID: {}",
                branch.getBranchId(), branch.getName(), franchiseId);
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
    public Mono<Franchise> addProduct(String franchiseId, String branchId, Product product) {
        log.debug("Agregando producto productId='{}' nombre='{}' a sucursal branchId='{}' en franquicia ID: {}",
                product.getProductId(), product.getName(), branchId, franchiseId);

        // Usar branchId en lugar de nombre para búsqueda
        Query query = Query.query(Criteria.where("id").is(franchiseId)
                .and("branches.branchId").is(branchId));

        Update update = new Update().push("branches.$[elem].products", mapper.toProductDocument(product));
        update.filterArray(Criteria.where("elem.branchId").is(branchId));

        return mongoTemplate.findAndModify(query, update,
                        new org.springframework.data.mongodb.core.FindAndModifyOptions().returnNew(true),
                        FranchiseDocument.class)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(FRANQUICIA_NO_ENCONTRADA)))
                .map(mapper::toEntity);
    }

    @Override
    @CircuitBreaker(name = SERVICE_OPERATION_MONGODB)
    public Mono<Franchise> removeProduct(String franchiseId, String branchId, String productId) {
        log.debug("Eliminando producto productId='{}' de sucursal branchId='{}' en franquicia ID: {}",
                productId, branchId, franchiseId);

        // Usar branchId para búsqueda
        Query query = Query.query(Criteria.where("id").is(franchiseId)
                .and("branches.branchId").is(branchId));

        // Eliminar producto por productId
        Update update = new Update().pull("branches.$[elem].products",
                Query.query(Criteria.where("productId").is(productId)));
        update.filterArray(Criteria.where("elem.branchId").is(branchId));

        return mongoTemplate.findAndModify(query, update,
                        new org.springframework.data.mongodb.core.FindAndModifyOptions().returnNew(true),
                        FranchiseDocument.class)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(FRANQUICIA_NO_ENCONTRADA)))
                .map(mapper::toEntity);
    }

    @Override
    @CircuitBreaker(name = SERVICE_OPERATION_MONGODB)
    public Mono<Franchise> updateStock(String franchiseId, String branchId, String productId, Integer newStock) {
        log.debug("Actualizando stock de producto productId='{}' a {} en sucursal branchId='{}', franquicia ID: {}",
                productId, newStock, branchId, franchiseId);

        // Usar branchId y productId para búsqueda
        Query query = Query.query(Criteria.where("id").is(franchiseId)
                .and("branches.branchId").is(branchId)
                .and("branches.products.productId").is(productId));

        Update update = new Update().set("branches.$[b].products.$[p].stock", newStock);
        update.filterArray(Criteria.where("b.branchId").is(branchId));
        update.filterArray(Criteria.where("p.productId").is(productId));

        return mongoTemplate.findAndModify(query, update,
                        new org.springframework.data.mongodb.core.FindAndModifyOptions().returnNew(true),
                        FranchiseDocument.class)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Producto no encontrado")))
                .map(mapper::toEntity);
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
    public Mono<Franchise> updateBranchName(String franchiseId, String branchId, String newName) {
        log.debug("Actualizando nombre de sucursal branchId='{}' a '{}' en franquicia ID: {}",
                branchId, newName, franchiseId);

        // Usar branchId para búsqueda
        Query query = Query.query(Criteria.where("id").is(franchiseId)
                .and("branches.branchId").is(branchId));
        Update update = new Update().set("branches.$.name", newName);

        return mongoTemplate.findAndModify(query, update,
                        new org.springframework.data.mongodb.core.FindAndModifyOptions().returnNew(true),
                        FranchiseDocument.class)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(FRANQUICIA_NO_ENCONTRADA)))
                .map(mapper::toEntity);
    }

    @Override
    @CircuitBreaker(name = SERVICE_OPERATION_MONGODB)
    public Mono<Franchise> updateProductName(String franchiseId, String branchId, String productId, String newName) {
        log.debug("Actualizando nombre de producto productId='{}' a '{}' en sucursal branchId='{}', franquicia ID: {}",
                productId, newName, branchId, franchiseId);

        // Usar branchId y productId para búsqueda
        Query query = Query.query(Criteria.where("id").is(franchiseId)
                .and("branches.branchId").is(branchId)
                .and("branches.products.productId").is(productId));

        Update update = new Update().set("branches.$[b].products.$[p].name", newName);
        update.filterArray(Criteria.where("b.branchId").is(branchId));
        update.filterArray(Criteria.where("p.productId").is(productId));

        return mongoTemplate.findAndModify(query, update,
                        new org.springframework.data.mongodb.core.FindAndModifyOptions().returnNew(true),
                        FranchiseDocument.class)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Producto no encontrado")))
                .map(mapper::toEntity);
    }

    @Override
    @CircuitBreaker(name = SERVICE_OPERATION_MONGODB)
    public Mono<Branch> findBranchById(String franchiseId, String branchId) {
        log.debug("Buscando sucursal branchId='{}' en franquicia ID: {}", branchId, franchiseId);
        return repository.findById(franchiseId)
                .map(mapper::toEntity)
                .flatMap(franchise -> franchise.getBranches().stream()
                        .filter(b -> b.getBranchId().equals(branchId))
                        .findFirst()
                        .map(Mono::just)
                        .orElse(Mono.error(new ResourceNotFoundException("Sucursal no encontrada con branchId: " + branchId)))
                );
    }

    @Override
    @CircuitBreaker(name = SERVICE_OPERATION_MONGODB)
    public Mono<Product> findProductById(String franchiseId, String branchId, String productId) {
        log.debug("Buscando producto productId='{}' en sucursal branchId='{}', franquicia ID: {}",
                productId, branchId, franchiseId);
        return findBranchById(franchiseId, branchId)
                .flatMap(branch -> branch.getProducts().stream()
                        .filter(p -> p.getProductId().equals(productId))
                        .findFirst()
                        .map(Mono::just)
                        .orElse(Mono.error(new ResourceNotFoundException("Producto no encontrado con productId: " + productId)))
                );
    }
}

