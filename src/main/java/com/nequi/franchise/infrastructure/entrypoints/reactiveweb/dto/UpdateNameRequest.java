package com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateNameRequest {
    @NotBlank
    private String name;
}