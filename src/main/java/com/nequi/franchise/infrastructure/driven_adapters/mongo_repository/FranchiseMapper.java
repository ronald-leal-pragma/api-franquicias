package com.nequi.franchise.infrastructure.driven_adapters.mongo_repository;

import com.nequi.franchise.domain.model.franchise.Branch;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.franchise.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FranchiseMapper {
    FranchiseDocument toDocument(Franchise franchise);
    Franchise toEntity(FranchiseDocument franchiseDocument);
    FranchiseDocument.BranchDocument toBranchDocument(Branch branch);
    FranchiseDocument.ProductDocument toProductDocument(Product product);
}
