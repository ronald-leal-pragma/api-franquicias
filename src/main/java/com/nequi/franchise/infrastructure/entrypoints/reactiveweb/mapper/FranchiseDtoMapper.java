package com.nequi.franchise.infrastructure.entrypoints.reactiveweb.mapper;

import com.nequi.franchise.domain.model.franchise.Branch;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.franchise.Product;
import com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto.AddBranchRequest;
import com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto.AddProductRequest;
import com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto.FranchiseRequest;
import org.springframework.stereotype.Component;

@Component
public class FranchiseDtoMapper {

    public Franchise toFranchise(FranchiseRequest request) {
        return Franchise.builder()
                .name(request.getName())
                .build();
    }

    public Branch toBranch(AddBranchRequest request) {
        return Branch.builder()
                .name(request.getName())
                .build();
    }

    public Product toProduct(AddProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .stock(request.getStock())
                .build();
    }
}

