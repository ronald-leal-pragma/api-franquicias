package com.nequi.franchise.domain.model.franchise;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representa una sucursal de la franquicia")
public class Branch {

    @Schema(description = "Identificador único de la sucursal", example = "507f1f77bcf86cd799439011")
    private String branchId;

    @Schema(description = "Nombre de la sucursal", example = "Sucursal Centro")
    private String name;

    @Builder.Default
    @Schema(description = "Lista de productos disponibles en la sucursal")
    private List<Product> products = new ArrayList<>();
}
