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
@Schema(description = "Representa una franquicia con sus sucursales")
public class Franchise {
    @Schema(description = "ID único de la franquicia", example = "123")
    private String id;

    @Schema(description = "Nombre de la franquicia", example = "Franquicia El Éxito")
    private String name;

    @Builder.Default
    @Schema(description = "Lista de sucursales de la franquicia")
    private List<Branch> branches = new ArrayList<>();
}
