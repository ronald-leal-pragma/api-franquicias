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
    private static final String FRANCHISE_ID = "franchiseId";
    private static final String BRANCH_NAME = "branchName";
    private static final String PRODUCT_NAME = "productName";

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final AddBranchUseCase addBranchUseCase;
    private final AddProductUseCase addProductUseCase;
    private final RemoveProductUseCase removeProductUseCase;
    private final UpdateStockUseCase updateStockUseCase;
    private final FindMaxStockUseCase findMaxStockUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;
    private final GlobalErrorHandler errorHandler;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(FranchiseRequest.class)
                .flatMap(dto -> {
                    Franchise franchiseDomain = Franchise.builder()
                            .name(dto.getName())
                            .build();

                    return createFranchiseUseCase.apply(franchiseDomain);
                })
                .flatMap(savedFranchise -> ServerResponse
                        .created(URI.create(BASE_PATH + savedFranchise.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedFranchise)
                )
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    public Mono<ServerResponse> addBranch(ServerRequest request) {
        String franchiseId = request.pathVariable(FRANCHISE_ID);

        return request.bodyToMono(AddBranchRequest.class)
                .flatMap(dto -> {
                    Branch branch = Branch.builder().name(dto.getName()).build();
                    return addBranchUseCase.apply(franchiseId, branch);
                })
                .flatMap(updatedFranchise -> ServerResponse.ok().bodyValue(updatedFranchise))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    public Mono<ServerResponse> addProduct(ServerRequest request) {
        String franchiseId = request.pathVariable(FRANCHISE_ID);
        String branchName = request.pathVariable(BRANCH_NAME);

        return request.bodyToMono(AddProductRequest.class)
                .flatMap(dto -> {
                    Product product = Product.builder()
                            .name(dto.getName())
                            .stock(dto.getStock())
                            .build();
                    return addProductUseCase.apply(franchiseId, branchName, product);
                })
                .flatMap(saved -> ServerResponse.ok().bodyValue(saved))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    public Mono<ServerResponse> removeProduct(ServerRequest request) {
        String franchiseId = request.pathVariable(FRANCHISE_ID);
        String branchName = request.pathVariable(BRANCH_NAME);
        String productName = request.pathVariable(PRODUCT_NAME);

        return removeProductUseCase.apply(franchiseId, branchName, productName)
                .flatMap(updatedFranchise -> ServerResponse.ok().bodyValue(updatedFranchise))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    public Mono<ServerResponse> updateStock(ServerRequest request) {
        String franchiseId = request.pathVariable(FRANCHISE_ID);
        String branchName = request.pathVariable(BRANCH_NAME);
        String productName = request.pathVariable(PRODUCT_NAME);

        return request.bodyToMono(UpdateStockRequest.class)
                .flatMap(dto -> updateStockUseCase.apply(franchiseId, branchName, productName, dto.getStock()))
                .flatMap(updatedFranchise -> ServerResponse.ok().bodyValue(updatedFranchise))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    public Mono<ServerResponse> getMaxStockProducts(ServerRequest request) {
        String franchiseId = request.pathVariable(FRANCHISE_ID);

        return ServerResponse.ok()
                .body(findMaxStockUseCase.apply(franchiseId), BranchProductResult.class)
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String id = request.pathVariable(FRANCHISE_ID);
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(dto -> updateFranchiseNameUseCase.apply(id, dto.getName()))
                .flatMap(f -> ServerResponse.ok().bodyValue(f))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        String id = request.pathVariable(FRANCHISE_ID);
        String branchName = request.pathVariable(BRANCH_NAME);
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(dto -> updateBranchNameUseCase.apply(id, branchName, dto.getName()))
                .flatMap(f -> ServerResponse.ok().bodyValue(f))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        String id = request.pathVariable(FRANCHISE_ID);
        String branchName = request.pathVariable(BRANCH_NAME);
        String productName = request.pathVariable(PRODUCT_NAME);

        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(dto -> updateProductNameUseCase.apply(id, branchName, productName, dto.getName()))
                .flatMap(f -> ServerResponse.ok().bodyValue(f))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }
}
