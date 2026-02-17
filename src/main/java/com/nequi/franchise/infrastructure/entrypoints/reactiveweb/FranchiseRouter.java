package com.nequi.franchise.infrastructure.entrypoints.reactiveweb;

import com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto.*;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.fn.builders.operation.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.function.Consumer;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;
import static org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

@Configuration
@RequiredArgsConstructor
public class FranchiseRouter {

    private final FranchiseHandler handler;
    private static final String TAG_FRANCHISE = "Franquicias";

    @Bean
    public RouterFunction<ServerResponse> franchiseRoutes() {
        return route()
                // 1. Crear Franquicia
                .POST("/api/franchises", handler::createFranchise, docCreateFranchise())

                // 2. Agregar Sucursal
                .POST("/api/franchises/{franchiseId}/branches", handler::addBranch, docAddBranch())

                // 3. Agregar Producto
                .POST("/api/franchises/{franchiseId}/branches/{branchName}/products", handler::addProduct, docAddProduct())

                // 4. Eliminar Producto
                .DELETE("/api/franchises/{franchiseId}/branches/{branchName}/products/{productName}", handler::removeProduct, docRemoveProduct())

                // 5. Actualizar Stock
                .PATCH("/api/franchises/{franchiseId}/branches/{branchName}/products/{productName}", handler::updateStock, docUpdateStock())

                // 6. Consultar Mayor Stock
                .GET("/api/franchises/{franchiseId}/products/max-stock", handler::getMaxStockProducts, docGetMaxStock())

                // 7. Actualizar Nombre Franquicia
                .PATCH("/api/franchises/{franchiseId}", handler::updateFranchiseName, docUpdateFranchiseName())

                // 8. Actualizar Nombre Sucursal
                .PATCH("/api/franchises/{franchiseId}/branches/{branchName}", handler::updateBranchName, docUpdateBranchName())

                // 9. Actualizar Nombre Producto
                .PATCH("/api/franchises/{franchiseId}/branches/{branchName}/products/{productName}/rename", handler::updateProductName, docUpdateProductName())

                .build();
    }

    // --- Métodos Privados para Documentación (Consumer<Builder>) ---

    private Consumer<Builder> docCreateFranchise() {
        return ops -> ops.tag(TAG_FRANCHISE)
                .operationId("createFranchise")
                .summary("Crear una nueva franquicia")
                .description("Crea una nueva franquicia en el sistema. El nombre debe ser único.")
                .requestBody(requestBodyBuilder().implementation(FranchiseRequest.class).required(true))
                .response(responseBuilder().responseCode("201").description("Franquicia creada exitosamente"))
                .response(responseBuilder().responseCode("409").description("Ya existe una franquicia con ese nombre"));
    }

    private Consumer<Builder> docAddBranch() {
        return ops -> ops.tag(TAG_FRANCHISE)
                .operationId("addBranch")
                .summary("Agregar sucursal a franquicia")
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("franchiseId").description("ID de la franquicia").example("507f1f..."))
                .requestBody(requestBodyBuilder().implementation(AddBranchRequest.class).required(true))
                .response(responseBuilder().responseCode("200").description("Sucursal agregada exitosamente"))
                .response(responseBuilder().responseCode("404").description("Franquicia no encontrada"));
    }

    private Consumer<Builder> docAddProduct() {
        return ops -> ops.tag(TAG_FRANCHISE)
                .operationId("addProduct")
                .summary("Agregar producto a sucursal")
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("franchiseId").example("507f1f..."))
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("branchName").example("Sucursal Centro"))
                .requestBody(requestBodyBuilder().implementation(AddProductRequest.class).required(true))
                .response(responseBuilder().responseCode("200").description("Producto agregado exitosamente"));
    }

    private Consumer<Builder> docRemoveProduct() {
        return ops -> ops.tag(TAG_FRANCHISE)
                .operationId("removeProduct")
                .summary("Eliminar producto de sucursal")
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("franchiseId").description("ID de la franquicia").example("507f1f77bcf86cd799439011"))
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("branchName").description("Nombre de la sucursal").example("Sucursal Centro"))
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("productName").description("Nombre del producto a eliminar").example("Laptop Dell"))
                .response(responseBuilder().responseCode("200").description("Producto eliminado exitosamente"))
                .response(responseBuilder().responseCode("404").description("Recurso no encontrado"));
    }

    private Consumer<Builder> docUpdateStock() {
        return ops -> ops.tag(TAG_FRANCHISE)
                .operationId("updateStock")
                .summary("Actualizar stock de producto")
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("franchiseId").description("ID de la franquicia").example("507f1f77bcf86cd799439011"))
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("branchName").description("Nombre de la sucursal").example("Sucursal Centro"))
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("productName").description("Nombre del producto").example("Laptop Dell"))
                .requestBody(requestBodyBuilder().implementation(UpdateStockRequest.class).required(true))
                .response(responseBuilder().responseCode("200").description("Stock actualizado"))
                .response(responseBuilder().responseCode("400").description("Stock inválido"));
    }

    private Consumer<Builder> docGetMaxStock() {
        return ops -> ops.tag(TAG_FRANCHISE)
                .operationId("getMaxStockProducts")
                .summary("Obtener productos con mayor stock por sucursal")
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("franchiseId").description("ID de la franquicia").example("507f1f77bcf86cd799439011"))
                .response(responseBuilder().responseCode("200").description("Lista generada exitosamente"))
                .response(responseBuilder().responseCode("404").description("Franquicia no encontrada"));
    }

    private Consumer<Builder> docUpdateFranchiseName() {
        return ops -> ops.tag(TAG_FRANCHISE)
                .operationId("updateFranchiseName")
                .summary("Actualizar nombre de franquicia")
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("franchiseId").description("ID de la franquicia").example("507f1f77bcf86cd799439011"))
                .requestBody(requestBodyBuilder().implementation(UpdateNameRequest.class).required(true))
                .response(responseBuilder().responseCode("200").description("Nombre actualizado"));
    }

    private Consumer<Builder> docUpdateBranchName() {
        return ops -> ops.tag(TAG_FRANCHISE)
                .operationId("updateBranchName")
                .summary("Actualizar nombre de sucursal")
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("franchiseId").description("ID de la franquicia").example("507f1f77bcf86cd799439011"))
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("branchName").description("Nombre actual de la sucursal").example("Sucursal Centro"))
                .requestBody(requestBodyBuilder().implementation(UpdateNameRequest.class).required(true))
                .response(responseBuilder().responseCode("200").description("Nombre actualizado"));
    }

    private Consumer<Builder> docUpdateProductName() {
        return ops -> ops.tag(TAG_FRANCHISE)
                .operationId("updateProductName")
                .summary("Actualizar nombre de producto")
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("franchiseId").description("ID de la franquicia").example("507f1f77bcf86cd799439011"))
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("branchName").description("Nombre de la sucursal").example("Sucursal Centro"))
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("productName").description("Nombre actual del producto").example("Laptop Dell"))
                .requestBody(requestBodyBuilder().implementation(UpdateNameRequest.class).required(true))
                .response(responseBuilder().responseCode("200").description("Nombre actualizado"));
    }
}

