package com.nequi.franchise.infrastructure.entrypoints.reactiveweb;

import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.usecase.franchise.CreateFranchiseUseCase;
import com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto.FranchiseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {
    private final CreateFranchiseUseCase createFranchiseUseCase;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(FranchiseRequest.class)
                .flatMap(dto -> {
                    Franchise franchiseDomain = Franchise.builder()
                            .name(dto.getName())
                            .build();

                    return createFranchiseUseCase.apply(franchiseDomain);
                })
                .flatMap(savedFranchise -> ServerResponse
                        .created(URI.create("/api/franchises/" + savedFranchise.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedFranchise)
                )
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
}
