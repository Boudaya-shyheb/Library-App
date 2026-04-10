package soyosa.inventaire.api;

import java.time.Instant;
import java.util.Map;

public record ApiResponse<T>(
        T data,
        String message,
        Map<String, Object> meta,
        Instant timestamp
) {
    public static <T> ApiResponse<T> ok(T data, String message, Map<String, Object> meta) {
        return new ApiResponse<>(data, message, meta, Instant.now());
    }
}
