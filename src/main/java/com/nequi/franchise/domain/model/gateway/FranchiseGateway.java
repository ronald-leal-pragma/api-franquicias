package com.nequi.franchise.domain.model.gateway;

import com.nequi.franchise.domain.model.franchise.Branch;
import com.nequi.franchise.domain.model.franchise.BranchProductResult;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.franchise.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Gateway agnóstico a la tecnología de persistencia.
 * Define operaciones sobre franquicias usando identificadores únicos (IDs)
 * en lugar de nombres, permitiendo una abstracción total de la base de datos.
 */
public interface FranchiseGateway {

    Mono<Franchise> saveFranchise(Franchise franchise);

    Mono<Franchise> findByName(String name);

    Mono<Franchise> findById(String id);

    Mono<Franchise> addBranch(String franchiseId, Branch branch);

    Mono<Franchise> addProduct(String franchiseId, String branchId, Product product);

    Mono<Franchise> removeProduct(String franchiseId, String branchId, String productId);

    Mono<Franchise> updateStock(String franchiseId, String branchId, String productId, Integer newStock);

    Flux<BranchProductResult> findMaxStockByBranch(String franchiseId);

    Mono<Franchise> updateFranchiseName(String franchiseId, String newName);

    Mono<Franchise> updateBranchName(String franchiseId, String branchId, String newName);

    Mono<Franchise> updateProductName(String franchiseId, String branchId, String productId, String newName);

    Mono<Branch> findBranchById(String franchiseId, String branchId);

    Mono<Product> findProductById(String franchiseId, String branchId, String productId);
}

