package com.library.apigateway.filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RequestPolicyFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(RequestPolicyFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    private final int burstCapacity;
    private final int refillTokensPerSecond;
    private final Map<String, BucketState> buckets = new ConcurrentHashMap<>();

    public RequestPolicyFilter(
            @Value("${app.rate-limit.burst-capacity:40}") int burstCapacity,
            @Value("${app.rate-limit.refill-tokens-per-second:20}") int refillTokensPerSecond
    ) {
        this.burstCapacity = burstCapacity;
        this.refillTokensPerSecond = refillTokensPerSecond;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = String.valueOf(request.getMethod());

        String correlationId = request.getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
            request = request.mutate().header(CORRELATION_ID_HEADER, correlationId).build();
        }

        if (!allowByRateLimit(clientId(exchange))) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().set(CORRELATION_ID_HEADER, correlationId);
            return exchange.getResponse().setComplete();
        }

        long start = System.currentTimeMillis();
        final String finalCorrelationId = correlationId;
        final String finalMethod = method;
        final String finalPath = path;
        ServerHttpRequest mutated = request;
        return chain.filter(exchange.mutate().request(mutated).build())
                .doOnSuccess(v -> log.info("[{}] {} {} -> {}", finalCorrelationId, finalMethod, finalPath,
                        exchange.getResponse().getStatusCode()))
                .doOnError(err -> log.error("[{}] {} {} failed: {}", finalCorrelationId, finalMethod, finalPath, err.getMessage()))
                .doFinally(signal -> log.debug("[{}] completed in {}ms", finalCorrelationId,
                        (System.currentTimeMillis() - start)));
    }

    private String clientId(ServerWebExchange exchange) {
        String forwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        if (exchange.getRequest().getRemoteAddress() != null) {
            return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }
        return "unknown-client";
    }

    private boolean allowByRateLimit(String key) {
        BucketState state = buckets.computeIfAbsent(key, k -> new BucketState(burstCapacity, Instant.now().getEpochSecond()));
        synchronized (state) {
            long now = Instant.now().getEpochSecond();
            long elapsedSeconds = Math.max(0, now - state.lastRefillSecond);
            if (elapsedSeconds > 0) {
                int refill = (int) (elapsedSeconds * refillTokensPerSecond);
                state.tokens = Math.min(burstCapacity, state.tokens + refill);
                state.lastRefillSecond = now;
            }
            if (state.tokens <= 0) {
                return false;
            }
            state.tokens -= 1;
            return true;
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private static final class BucketState {
        private int tokens;
        private long lastRefillSecond;

        private BucketState(int tokens, long lastRefillSecond) {
            this.tokens = tokens;
            this.lastRefillSecond = lastRefillSecond;
        }
    }
}
