package com.nequi.franchise.domain.model.gateway;

import com.nequi.franchise.domain.model.franchise.Branch;
import com.nequi.franchise.domain.model.franchise.BranchProductResult;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.franchise.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseGateway {

    Mono<Franchise> saveFranchise(Franchise franchise);

    Mono<Franchise> findByName(String name);

    Mono<Franchise> findById(String id);

    Mono<Franchise> addBranch(String franchiseId, Branch branch);

    Mono<Franchise> addProduct(String franchiseId, String branchName, Product product);

    Mono<Franchise> removeProduct(String franchiseId, String branchName, String productName);

    Mono<Franchise> updateStock(String franchiseId, String branchName, String productName, Integer newStock);

    Flux<BranchProductResult> findMaxStockByBranch(String franchiseId);

    Mono<Franchise> updateFranchiseName(String franchiseId, String newName);

    Mono<Franchise> updateBranchName(String franchiseId, String currentName, String newName);

    Mono<Franchise> updateProductName(String franchiseId, String branchName, String currentName, String newName);

}