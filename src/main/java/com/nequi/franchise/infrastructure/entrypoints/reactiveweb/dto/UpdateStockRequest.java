package com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Datos para actualizar el stock de un producto")
public class UpdateStockRequest {
    @NotNull
    @Min(0)
    @Schema(description = "Nueva cantidad de stock", example = "75", minimum = "0")
    private Integer stock;
}
