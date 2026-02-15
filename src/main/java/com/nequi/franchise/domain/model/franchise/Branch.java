package com.nequi.franchise.domain.model.franchise;

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
public class Branch {

    private String name;

    @Builder.Default
    private List<Product> products = new ArrayList<>();
}
