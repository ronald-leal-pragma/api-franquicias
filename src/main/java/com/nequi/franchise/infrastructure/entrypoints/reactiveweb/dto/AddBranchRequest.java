package com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Datos para agregar una nueva sucursal")
public class AddBranchRequest {
    @NotBlank
    @Schema(description = "Nombre de la sucursal", example = "Sucursal Centro")
    private String name;
}
