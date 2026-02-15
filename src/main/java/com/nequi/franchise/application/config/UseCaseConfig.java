package com.nequi.franchise.application.config;

import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import com.nequi.franchise.domain.usecase.franchise.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {
    @Bean
    public CreateFranchiseUseCase createFranchiseUseCase(FranchiseGateway gateway) {
        return new CreateFranchiseUseCase(gateway);
    }

    @Bean
    public AddBranchUseCase addBranchUseCase(FranchiseGateway gateway) {
        return new AddBranchUseCase(gateway);
    }

    @Bean
    public AddProductUseCase addProductUseCase(FranchiseGateway gateway) {
        return new AddProductUseCase(gateway);
    }

    @Bean
    public RemoveProductUseCase removeProductUseCase(FranchiseGateway gateway) {
        return new RemoveProductUseCase(gateway);
    }

    @Bean
    public UpdateStockUseCase updateStockUseCase(FranchiseGateway gateway) {
        return new UpdateStockUseCase(gateway);
    }

    @Bean
    public FindMaxStockUseCase findMaxStockUseCase(FranchiseGateway gateway) {
        return new FindMaxStockUseCase(gateway);
    }

    @Bean
    public UpdateFranchiseNameUseCase updateFranchiseNameUseCase(FranchiseGateway gateway) {
        return new UpdateFranchiseNameUseCase(gateway);
    }
    @Bean
    public UpdateBranchNameUseCase updateBranchNameUseCase(FranchiseGateway gateway) {
        return new UpdateBranchNameUseCase(gateway);
    }
    @Bean
    public UpdateProductNameUseCase updateProductNameUseCase(FranchiseGateway gateway) {
        return new UpdateProductNameUseCase(gateway);
    }
}

