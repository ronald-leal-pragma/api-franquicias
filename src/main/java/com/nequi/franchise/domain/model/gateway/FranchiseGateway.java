package com.nequi.franchise.domain.model.gateway;

import com.nequi.franchise.domain.model.franchise.Branch;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.franchise.Product;
import reactor.core.publisher.Mono;

public interface FranchiseGateway {
    // Operaciones a nivel de Franquicia
    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> findById(String id);

    // Operaciones atómicas sobre elementos internos (Aunque sean internos, el Gateway expone la intención)
    // Nota: La implementación en infraestructura usará consultas específicas de Mongo para no traer todo el objeto
    Mono<Void> updateStock(String franchiseId, String branchName, String productName, Integer stock);

    Mono<Void> addBranch(String franchiseId, Branch branch);

    Mono<Void> addProduct(String franchiseId, String branchName, Product product);
}
