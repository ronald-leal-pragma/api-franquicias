package com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddProductRequest {
    private String name;
    private Integer stock;
}
