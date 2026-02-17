package com.nequi.franchise.domain.model.franchise;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representa un producto en el inventario de una sucursal")
public class Product {

    @Schema(description = "Identificador único del producto", example = "507f1f77bcf86cd799439012")
    private String productId;

    @Schema(description = "Nombre del producto", example = "Laptop Dell XPS 15")
    private String name;

    @Schema(description = "Cantidad disponible en stock", example = "50", minimum = "0")
    private Integer stock;
}