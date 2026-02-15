package com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseRequest {
    @NotBlank(message = "Name is required")
    private String name;
}
