package com.nequi.franchise.infrastructure.entrypoints.reactiveweb.helper;

import com.nequi.franchise.domain.exception.ResourceNotFoundException;
import com.nequi.franchise.domain.model.franchise.Branch;
import com.nequi.franchise.domain.model.franchise.Product;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Helper para resolver nombres a IDs en operaciones de API.
 * Mantiene compatibilidad con endpoints que usan nombres en la URL.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FranchiseIdResolver {

    private final FranchiseGateway franchiseGateway;

    /**
     * Resuelve branchName a branchId dentro de una franquicia.
     */
    public Mono<String> resolveBranchId(String franchiseId, String branchName) {
        log.debug("Resolviendo branchName='{}' a branchId en franchiseId={}", branchName, franchiseId);

        return franchiseGateway.findById(franchiseId)
                .flatMap(franchise -> franchise.getBranches().stream()
                        .filter(b -> b.getName().equalsIgnoreCase(branchName))
                        .findFirst()
                        .map(Branch::getBranchId)
                        .map(Mono::just)
                        .orElse(Mono.error(new ResourceNotFoundException("Sucursal no encontrada con nombre: " + branchName)))
                );
    }

    /**
     * Resuelve productName a productId dentro de una sucursal.
     */
    public Mono<String> resolveProductId(String franchiseId, String branchId, String productName) {
        log.debug("Resolviendo productName='{}' a productId en branchId={}, franchiseId={}",
                productName, branchId, franchiseId);

        return franchiseGateway.findBranchById(franchiseId, branchId)
                .flatMap(branch -> branch.getProducts().stream()
                        .filter(p -> p.getName().equalsIgnoreCase(productName))
                        .findFirst()
                        .map(Product::getProductId)
                        .map(Mono::just)
                        .orElse(Mono.error(new ResourceNotFoundException("Producto no encontrado con nombre: " + productName)))
                );
    }
}

