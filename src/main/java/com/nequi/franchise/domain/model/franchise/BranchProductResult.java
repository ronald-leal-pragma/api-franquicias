package com.nequi.franchise.domain.model.franchise;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resultado que muestra el producto con mayor stock de una sucursal")
public class BranchProductResult {
    @Schema(description = "Nombre de la sucursal", example = "Sucursal Centro")
    private String branchName;

    @Schema(description = "Producto con mayor stock en la sucursal")
    private Product product;
}