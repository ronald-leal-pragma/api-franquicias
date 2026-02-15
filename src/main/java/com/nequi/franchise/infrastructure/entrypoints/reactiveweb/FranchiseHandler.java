package com.nequi.franchise.infrastructure.entrypoints.reactiveweb;

import com.nequi.franchise.domain.model.franchise.Branch;
import com.nequi.franchise.domain.model.franchise.BranchProductResult;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.franchise.Product;
import com.nequi.franchise.domain.usecase.franchise.*;
import com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {
    private static final String BASE_PATH = "/api/franchises";
    private static final String BRANCH_PATH = BASE_PATH + "/{franchiseId}/branches";
    private static final String PRODUCT_PATH = BRANCH_PATH + "/{branchName}/products";
    private static final String STOCK_PATH = PRODUCT_PATH + "/{productName}/stock";
    private static final String MAX_STOCK_PATH = BASE_PATH + "/{franchiseId}/products/max-stock";
    private static final String UPDATE_FRANCHISE_NAME_PATH = BASE_PATH + "/{franchiseId}";
    private static final String UPDATE_BRANCH_NAME_PATH = BRANCH_PATH + "/{branchName}";
    private static final String UPDATE_PRODUCT_NAME_PATH = PRODUCT_PATH + "/{productName}/rename";

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final AddBranchUseCase addBranchUseCase;
    private final AddProductUseCase addProductUseCase;
    private final RemoveProductUseCase removeProductUseCase;
    private final UpdateStockUseCase updateStockUseCase;
    private final FindMaxStockUseCase findMaxStockUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(FranchiseRequest.class)
                .flatMap(dto -> {
                    Franchise franchiseDomain = Franchise.builder()
                            .name(dto.getName())
                            .build();

                    return createFranchiseUseCase.apply(franchiseDomain);
                })
                .flatMap(savedFranchise -> ServerResponse
                        .created(URI.create("/api/franchises/" + savedFranchise.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedFranchise)
                )
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> addBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");

        return request.bodyToMono(AddBranchRequest.class)
                .flatMap(dto -> {
                    Branch branch = Branch.builder().name(dto.getName()).build();
                    return addBranchUseCase.apply(franchiseId, branch);
                })
                .flatMap(updatedFranchise -> ServerResponse.ok().bodyValue(updatedFranchise))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> addProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchName = request.pathVariable("branchName");

        return request.bodyToMono(AddProductRequest.class)
                .flatMap(dto -> {
                    Product product = Product.builder()
                            .name(dto.getName())
                            .stock(dto.getStock())
                            .build();
                    return addProductUseCase.apply(franchiseId, branchName, product);
                })
                .flatMap(saved -> ServerResponse.ok().bodyValue(saved))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> removeProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchName = request.pathVariable("branchName");
        String productName = request.pathVariable("productName");

        return removeProductUseCase.apply(franchiseId, branchName, productName)
                .flatMap(updatedFranchise -> ServerResponse.ok().bodyValue(updatedFranchise))
                .onErrorResume(e -> ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> updateStock(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchName = request.pathVariable("branchName");
        String productName = request.pathVariable("productName");

        return request.bodyToMono(UpdateStockRequest.class)
                .flatMap(dto -> updateStockUseCase.apply(franchiseId, branchName, productName, dto.getStock()))
                .flatMap(updatedFranchise -> ServerResponse.ok().bodyValue(updatedFranchise))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> getMaxStockProducts(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");

        return ServerResponse.ok()
                .body(findMaxStockUseCase.apply(franchiseId), BranchProductResult.class)
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String id = request.pathVariable("franchiseId");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(dto -> updateFranchiseNameUseCase.apply(id, dto.getName()))
                .flatMap(f -> ServerResponse.ok().bodyValue(f))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        String id = request.pathVariable("franchiseId");
        String branchName = request.pathVariable("branchName");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(dto -> updateBranchNameUseCase.apply(id, branchName, dto.getName()))
                .flatMap(f -> ServerResponse.ok().bodyValue(f))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        String id = request.pathVariable("franchiseId");
        String branchName = request.pathVariable("branchName");
        String productName = request.pathVariable("productName");

        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(dto -> updateProductNameUseCase.apply(id, branchName, productName, dto.getName()))
                .flatMap(f -> ServerResponse.ok().bodyValue(f))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
}
