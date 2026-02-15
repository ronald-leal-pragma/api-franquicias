package com.nequi.franchise.application.config;

import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import com.nequi.franchise.domain.usecase.franchise.CreateFranchiseUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {
    // Aquí registramos el Caso de Uso como un Bean de Spring
    // Spring inyectará automáticamente la implementación del 'gateway' que definamos más tarde en infraestructura
    @Bean
    public CreateFranchiseUseCase createFranchiseUseCase(FranchiseGateway gateway) {
        return new CreateFranchiseUseCase(gateway);
    }
}
