package com.fooddelivery.api_gateway.filter;

import com.fooddelivery.api_gateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    private static final List<String> PUBLIC_ROUTES = List.of(
            "/auth/register",
            "/auth/login",
            "/api/menu/available",
            "/api/restaurants"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();

        // ✅ Allow public GET routes
        if (isPublicGetRoute(path, method)) {
            return chain.filter(exchange);
        }

        // ✅ Allow auth routes
        if (isAuthRoute(path)) {
            return chain.filter(exchange);
        }

        // ✅ Check Authorization header
        String authHeader = request.getHeaders()
                .getFirst("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {
            return unauthorizedResponse(exchange,
                    "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        // ✅ Validate token
        if (!jwtUtil.isTokenValid(token)) {
            return unauthorizedResponse(exchange,
                    "Invalid or expired token");
        }

        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            // ✅ Extract userId from token
            String userId = jwtUtil.extractUserId(token);


            // ✅ Role based access control
            if (isRestaurantOwnerRoute(path, method)) {
                if (!role.equals("RESTAURANT_OWNER")) {
                    return forbiddenResponse(exchange,
                            "Access denied! Only RESTAURANT_OWNER can perform this action.");
                }
            }

            // ✅ Only CUSTOMER can place orders
            if (path.startsWith("/api/orders") &&
                    method.equals("POST")) {
                if (!role.equals("CUSTOMER")) {
                    return forbiddenResponse(exchange,
                            "Access denied! Only CUSTOMER can place orders.");
                }
            }

            // ✅ Only AGENT can update delivery
            if (path.startsWith("/api/delivery") &&
                    method.equals("PUT")) {
                if (!role.equals("AGENT")) {
                    return forbiddenResponse(exchange,
                            "Access denied! Only AGENT can update delivery.");
                }
            }

            // ✅ Pass user info to downstream services
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Email", email)
                    .header("X-User-Role", role)
                    .header("X-User-Id", userId != null
                            ? userId : "") // ✅ Added userId!
                    .build();

            return chain.filter(
                    exchange.mutate()
                            .request(modifiedRequest)
                            .build());

        } catch (Exception e) {
            return unauthorizedResponse(exchange,
                    "Token processing failed");
        }
    }

    private boolean isPublicGetRoute(String path, String method) {
        if (!method.equals("GET")) return false;
        return path.equals("/api/menu/available") ||
                path.startsWith("/api/restaurants") ||
                path.startsWith("/api/menu/restaurant/");
    }

    private boolean isAuthRoute(String path) {
        return path.equals("/auth/register") ||
                path.equals("/auth/login");
    }

    private boolean isRestaurantOwnerRoute(
            String path, String method) {
        if (path.equals("/api/restaurants") &&
                method.equals("POST")) return true;
        if (path.startsWith("/api/restaurants/") &&
                method.equals("PUT")) return true;
        if (path.startsWith("/api/restaurants/") &&
                method.equals("DELETE")) return true;
        if (path.equals("/api/menu") &&
                method.equals("POST")) return true;
        if (path.startsWith("/api/menu/") &&
                method.equals("PUT")) return true;
        if (path.startsWith("/api/menu/") &&
                method.equals("DELETE")) return true;
        return false;
    }

    private Mono<Void> unauthorizedResponse(
            ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders()
                .add("Content-Type", "application/json");
        var buffer = response.bufferFactory()
                .wrap(("{\"error\": \"" + message + "\"}")
                        .getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    private Mono<Void> forbiddenResponse(
            ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders()
                .add("Content-Type", "application/json");
        var buffer = response.bufferFactory()
                .wrap(("{\"error\": \"" + message + "\"}")
                        .getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}