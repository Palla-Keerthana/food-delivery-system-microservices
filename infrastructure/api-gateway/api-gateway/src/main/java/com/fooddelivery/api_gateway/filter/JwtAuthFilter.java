package com.fooddelivery.api_gateway.filter;

import com.fooddelivery.api_gateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
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

    // ✅ Public routes — no token needed
    private static final List<String> PUBLIC_ROUTES = List.of(
            "/auth/register",
            "/auth/login",
            "/api/menu/available",  // ← customers can view without login
            "/api/restaurants"      // ← anyone can view restaurants
    );

    // ✅ Restaurant Owner only routes
    private static final List<String> RESTAURANT_OWNER_ROUTES = List.of(
            "/api/restaurants",     // POST only
            "/api/menu"             // POST, PUT, DELETE only
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();

        // ✅ Allow GET requests to public routes
        if (isPublicGetRoute(path, method)) {
            return chain.filter(exchange);
        }

        // ✅ Allow auth routes always
        if (isAuthRoute(path)) {
            return chain.filter(exchange);
        }

        // ✅ Check Authorization header
        String authHeader = request.getHeaders()
                .getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
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

            // ✅ Role based access control
            // Only RESTAURANT_OWNER can POST/PUT/DELETE menu and restaurant
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

            // ✅ Only AGENT can update delivery status
            if (path.startsWith("/api/delivery") &&
                    method.equals("PUT")) {
                if (!role.equals("AGENT")) {
                    return forbiddenResponse(exchange,
                            "Access denied! Only AGENT can update delivery status.");
                }
            }

            // ✅ Add user info to headers
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Email", email)
                    .header("X-User-Role", role)
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

    // ✅ Check if public GET route
    private boolean isPublicGetRoute(String path, String method) {
        if (!method.equals("GET")) return false;
        return path.equals("/api/menu/available") ||
                path.startsWith("/api/restaurants") ||
                path.startsWith("/api/menu/restaurant/");
    }

    // ✅ Check if auth route
    private boolean isAuthRoute(String path) {
        return path.equals("/auth/register") ||
                path.equals("/auth/login");
    }

    // ✅ Check if restaurant owner only route
    private boolean isRestaurantOwnerRoute(String path, String method) {
        // POST /api/restaurants → register restaurant
        if (path.equals("/api/restaurants") &&
                method.equals("POST")) return true;

        // PUT /api/restaurants/{id} → update restaurant
        if (path.startsWith("/api/restaurants/") &&
                method.equals("PUT")) return true;

        // DELETE /api/restaurants/{id} → delete restaurant
        if (path.startsWith("/api/restaurants/") &&
                method.equals("DELETE")) return true;

        // POST /api/menu → add menu item
        if (path.equals("/api/menu") &&
                method.equals("POST")) return true;

        // PUT /api/menu/{id} → update menu item
        if (path.startsWith("/api/menu/") &&
                method.equals("PUT")) return true;

        // DELETE /api/menu/{id} → delete menu item
        if (path.startsWith("/api/menu/") &&
                method.equals("DELETE")) return true;

        return false;
    }

    // ✅ 401 Unauthorized response
    private Mono<Void> unauthorizedResponse(
            ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        var buffer = response.bufferFactory()
                .wrap(("{\"error\": \"" + message + "\"}").getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    // ✅ 403 Forbidden response
    private Mono<Void> forbiddenResponse(
            ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().add("Content-Type", "application/json");
        var buffer = response.bufferFactory()
                .wrap(("{\"error\": \"" + message + "\"}").getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}