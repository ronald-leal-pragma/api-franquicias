package com.nequi.franchise.domain.model.franchise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchProductResult {
    private String branchName;
    private Product product;
}