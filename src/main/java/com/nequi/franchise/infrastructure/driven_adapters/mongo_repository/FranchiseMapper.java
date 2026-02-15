package com.nequi.franchise.infrastructure.driven_adapters.mongo_repository;

import com.nequi.franchise.domain.model.franchise.Franchise;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FranchiseMapper {
    FranchiseDocument toDocument(Franchise franchise);
    Franchise toEntity(FranchiseDocument franchiseDocument);
}
