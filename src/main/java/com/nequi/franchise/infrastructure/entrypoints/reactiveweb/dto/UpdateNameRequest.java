package com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Datos para actualizar un nombre")
public class UpdateNameRequest {
    @NotBlank
    @Schema(description = "Nuevo nombre", example = "Nombre Actualizado")
    private String name;
}