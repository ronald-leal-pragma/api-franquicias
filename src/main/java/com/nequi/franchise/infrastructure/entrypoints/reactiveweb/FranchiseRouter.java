package com.nequi.franchise.infrastructure.entrypoints.reactiveweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class FranchiseRouter {
    @Bean
    public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandler handler) {
        return route(POST("/api/franchises"), handler::createFranchise)
                .andRoute(POST("/api/franchises/{franchiseId}/branches"), handler::addBranch)
                .andRoute(POST("/api/franchises/{franchiseId}/branches/{branchName}/products"), handler::addProduct)
                .andRoute(DELETE("/api/franchises/{franchiseId}/branches/{branchName}/products/{productName}"),
                        handler::removeProduct)
                .andRoute(PATCH("/api/franchises/{franchiseId}/branches/{branchName}/products/{productName}"), handler::updateStock)
                .andRoute(GET("/api/franchises/{franchiseId}/products/max-stock"), handler::getMaxStockProducts)
                .andRoute(PATCH("/api/franchises/{franchiseId}"), handler::updateFranchiseName)
                .andRoute(PATCH("/api/franchises/{franchiseId}/branches/{branchName}"), handler::updateBranchName)
                .andRoute(PATCH("/api/franchises/{franchiseId}/branches/{branchName}/products/{productName}/rename"), handler::updateProductName);
    }
}
