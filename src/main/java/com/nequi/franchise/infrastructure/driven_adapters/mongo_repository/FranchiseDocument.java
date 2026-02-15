package com.nequi.franchise.infrastructure.driven_adapters.mongo_repository;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "franchises")
public class FranchiseDocument {
    @Id
    private String id;
    private String name;
    private List<BranchDocument> branches;

    @Data
    @NoArgsConstructor
    public static class BranchDocument {
        private String name;
        private List<ProductDocument> products;
    }

    @Data
    @NoArgsConstructor
    public static class ProductDocument {
        private String name;
        private Integer stock;
    }
}
