package com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Datos para agregar un nuevo producto")
public class AddProductRequest {
    @Schema(description = "Nombre del producto", example = "Laptop Dell XPS 15")
    private String name;

    @Schema(description = "Cantidad inicial de stock", example = "50", minimum = "0")
    private Integer stock;
}
