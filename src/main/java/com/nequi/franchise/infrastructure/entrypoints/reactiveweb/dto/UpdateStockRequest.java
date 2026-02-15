package com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateStockRequest {
    @NotNull
    @Min(0)
    private Integer stock;
}
