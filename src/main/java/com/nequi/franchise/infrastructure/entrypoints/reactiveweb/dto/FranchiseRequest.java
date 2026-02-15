package com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear una nueva franquicia")
public class FranchiseRequest {
    @NotBlank(message = "Name is required")
    @Schema(description = "Nombre de la franquicia", example = "Franquicia El Éxito")
    private String name;
}
