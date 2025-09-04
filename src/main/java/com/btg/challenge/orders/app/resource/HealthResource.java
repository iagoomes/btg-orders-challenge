package com.btg.challenge.orders.app.resource;

import com.btg.challenge.orders.api.HealthApiDelegate;
import com.btg.challenge.orders.model.HealthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class HealthResource implements HealthApiDelegate {

    @Override
    public CompletableFuture<ResponseEntity<HealthResponse>> healthCheck() {
        return CompletableFuture.supplyAsync(() -> {
            log.debug("Performing health check");
            HealthResponse healthResponse = new HealthResponse();
            healthResponse.setStatus("UP");
            return ResponseEntity.ok(healthResponse);
        });
    }
}
