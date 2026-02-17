package com.nequi.franchise.infrastructure.entrypoints.reactiveweb;

import com.nequi.franchise.domain.model.franchise.BranchProductResult;
import com.nequi.franchise.domain.usecase.franchise.*;
import com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto.*;
import com.nequi.franchise.infrastructure.entrypoints.reactiveweb.mapper.FranchiseDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
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
    private final FranchiseDtoMapper mapper;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(FranchiseRequest.class)
                .doOnSubscribe(s -> log.info("Method: createFranchise - Input: path={}", request.path()))
                .doOnNext(dto -> log.info("Method: createFranchise - Request body: {}", dto))
                .map(mapper::toFranchise)
                .flatMap(createFranchiseUseCase::apply)
                .flatMap(savedFranchise -> ServerResponse
                        .created(URI.create(BASE_PATH + savedFranchise.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedFranchise)
                )
                .doOnSuccess(response -> log.info("Method: createFranchise - Output: status=201"))
                .doOnError(error -> log.error("Method: createFranchise - Error: {}", error.getMessage(), error))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    public Mono<ServerResponse> addBranch(ServerRequest request) {
        String franchiseId = request.pathVariable(FRANCHISE_ID);

        return request.bodyToMono(AddBranchRequest.class)
                .doOnSubscribe(s -> log.info("Method: addBranch - Input: franchiseId={}, path={}", franchiseId, request.path()))
                .doOnNext(dto -> log.info("Method: addBranch - Request body: {}", dto))
                .map(mapper::toBranch)
                .flatMap(branch -> addBranchUseCase.apply(franchiseId, branch))
                .flatMap(updatedFranchise -> ServerResponse.ok().bodyValue(updatedFranchise))
                .doOnSuccess(response -> log.info("Method: addBranch - Output: status=200, franchiseId={}", franchiseId))
                .doOnError(error -> log.error("Method: addBranch - Error: franchiseId={}, message={}", franchiseId, error.getMessage(), error))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    public Mono<ServerResponse> addProduct(ServerRequest request) {
        String franchiseId = request.pathVariable(FRANCHISE_ID);
        String branchName = request.pathVariable(BRANCH_NAME);

        return request.bodyToMono(AddProductRequest.class)
                .doOnSubscribe(s -> log.info("Method: addProduct - Input: franchiseId={}, branchName={}, path={}", franchiseId, branchName, request.path()))
                .doOnNext(dto -> log.info("Method: addProduct - Request body: {}", dto))
                .map(mapper::toProduct)
                .flatMap(product -> addProductUseCase.apply(franchiseId, branchName, product))
                .flatMap(saved -> ServerResponse.ok().bodyValue(saved))
                .doOnSuccess(response -> log.info("Method: addProduct - Output: status=200, franchiseId={}, branchName={}", franchiseId, branchName))
                .doOnError(error -> log.error("Method: addProduct - Error: franchiseId={}, branchName={}, message={}", franchiseId, branchName, error.getMessage(), error))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    public Mono<ServerResponse> removeProduct(ServerRequest request) {
        String franchiseId = request.pathVariable(FRANCHISE_ID);
        String branchName = request.pathVariable(BRANCH_NAME);
        String productName = request.pathVariable(PRODUCT_NAME);

        return Mono.just(productName)
                .doOnSubscribe(s -> log.info("Method: removeProduct - Input: franchiseId={}, branchName={}, productName={}, path={}", franchiseId, branchName, productName, request.path()))
                .flatMap(pn -> removeProductUseCase.apply(franchiseId, branchName, pn))
                .flatMap(updatedFranchise -> ServerResponse.ok().bodyValue(updatedFranchise))
                .doOnSuccess(response -> log.info("Method: removeProduct - Output: status=200, franchiseId={}, branchName={}, productName={}", franchiseId, branchName, productName))
                .doOnError(error -> log.error("Method: removeProduct - Error: franchiseId={}, branchName={}, productName={}, message={}", franchiseId, branchName, productName, error.getMessage(), error))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    public Mono<ServerResponse> updateStock(ServerRequest request) {
        String franchiseId = request.pathVariable(FRANCHISE_ID);
        String branchName = request.pathVariable(BRANCH_NAME);
        String productName = request.pathVariable(PRODUCT_NAME);

        return request.bodyToMono(UpdateStockRequest.class)
                .doOnSubscribe(s -> log.info("Method: updateStock - Input: franchiseId={}, branchName={}, productName={}, path={}", franchiseId, branchName, productName, request.path()))
                .doOnNext(dto -> log.info("Method: updateStock - Request body: {}", dto))
                .flatMap(dto -> updateStockUseCase.apply(franchiseId, branchName, productName, dto.getStock()))
                .flatMap(updatedFranchise -> ServerResponse.ok().bodyValue(updatedFranchise))
                .doOnSuccess(response -> log.info("Method: updateStock - Output: status=200, franchiseId={}, branchName={}, productName={}", franchiseId, branchName, productName))
                .doOnError(error -> log.error("Method: updateStock - Error: franchiseId={}, branchName={}, productName={}, message={}", franchiseId, branchName, productName, error.getMessage(), error))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    public Mono<ServerResponse> getMaxStockProducts(ServerRequest request) {
        String franchiseId = request.pathVariable(FRANCHISE_ID);

        log.info("Method: getMaxStockProducts - Input: franchiseId={}, path={}", franchiseId, request.path());

        return ServerResponse.ok()
                .body(findMaxStockUseCase.apply(franchiseId), BranchProductResult.class)
                .switchIfEmpty(ServerResponse.notFound().build())
                .doOnSuccess(response -> log.info("Method: getMaxStockProducts - Output: status={}, franchiseId={}", response != null ? response.statusCode() : "404", franchiseId))
                .doOnError(error -> log.error("Method: getMaxStockProducts - Error: franchiseId={}, message={}", franchiseId, error.getMessage(), error))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String id = request.pathVariable(FRANCHISE_ID);
        return request.bodyToMono(UpdateNameRequest.class)
                .doOnSubscribe(s -> log.info("Method: updateFranchiseName - Input: franchiseId={}, path={}", id, request.path()))
                .doOnNext(dto -> log.info("Method: updateFranchiseName - Request body: {}", dto))
                .flatMap(dto -> updateFranchiseNameUseCase.apply(id, dto.getName()))
                .flatMap(f -> ServerResponse.ok().bodyValue(f))
                .doOnSuccess(response -> log.info("Method: updateFranchiseName - Output: status=200, franchiseId={}", id))
                .doOnError(error -> log.error("Method: updateFranchiseName - Error: franchiseId={}, message={}", id, error.getMessage(), error))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        String id = request.pathVariable(FRANCHISE_ID);
        String branchName = request.pathVariable(BRANCH_NAME);
        return request.bodyToMono(UpdateNameRequest.class)
                .doOnSubscribe(s -> log.info("Method: updateBranchName - Input: franchiseId={}, branchName={}, path={}", id, branchName, request.path()))
                .doOnNext(dto -> log.info("Method: updateBranchName - Request body: {}", dto))
                .flatMap(dto -> updateBranchNameUseCase.apply(id, branchName, dto.getName()))
                .flatMap(f -> ServerResponse.ok().bodyValue(f))
                .doOnSuccess(response -> log.info("Method: updateBranchName - Output: status=200, franchiseId={}, branchName={}", id, branchName))
                .doOnError(error -> log.error("Method: updateBranchName - Error: franchiseId={}, branchName={}, message={}", id, branchName, error.getMessage(), error))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        String id = request.pathVariable(FRANCHISE_ID);
        String branchName = request.pathVariable(BRANCH_NAME);
        String productName = request.pathVariable(PRODUCT_NAME);

        return request.bodyToMono(UpdateNameRequest.class)
                .doOnSubscribe(s -> log.info("Method: updateProductName - Input: franchiseId={}, branchName={}, productName={}, path={}", id, branchName, productName, request.path()))
                .doOnNext(dto -> log.info("Method: updateProductName - Request body: {}", dto))
                .flatMap(dto -> updateProductNameUseCase.apply(id, branchName, productName, dto.getName()))
                .flatMap(f -> ServerResponse.ok().bodyValue(f))
                .doOnSuccess(response -> log.info("Method: updateProductName - Output: status=200, franchiseId={}, branchName={}, productName={}", id, branchName, productName))
                .doOnError(error -> log.error("Method: updateProductName - Error: franchiseId={}, branchName={}, productName={}, message={}", id, branchName, productName, error.getMessage(), error))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }
}
