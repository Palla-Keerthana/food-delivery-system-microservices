package com.fooddelivery.api_gateway.filter;

import com.fooddelivery.api_gateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
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

/**
 * Global JWT authentication filter for the API Gateway.
 * Intercepts every incoming request and validates the JWT token
 * before forwarding to the appropriate microservice.
 * Implements role-based access control for protected routes.
 *
 * <p>Filter execution order is set to {@code -1} to ensure
 * it runs before all other filters.</p>
 *
 * <p>Public routes that bypass token validation:</p>
 * <ul>
 *   <li>{@code POST /auth/register}</li>
 *   <li>{@code POST /auth/login}</li>
 *   <li>{@code GET /api/menu/available}</li>
 *   <li>{@code GET /api/restaurants}</li>
 * </ul>
 */
@Component
@Slf4j
public class JwtAuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * List of public routes that do not require JWT token.
     * Requests to these routes are forwarded without authentication.
     */
    private static final List<String> PUBLIC_ROUTES = List.of(
            "/auth/register",
            "/auth/login",
            "/api/menu/available",
            "/api/restaurants"
    );

    /**
     * List of routes accessible only by RESTAURANT_OWNER role.
     * Write operations (POST, PUT, DELETE) on these routes
     * are restricted to restaurant owners.
     */
    private static final List<String> RESTAURANT_OWNER_ROUTES = List.of(
            "/api/restaurants",
            "/api/menu"
    );

    /**
     * Main filter method that intercepts every incoming request.
     * Performs the following checks in order:
     * <ol>
     *   <li>Allows public GET routes without token</li>
     *   <li>Allows auth routes without token</li>
     *   <li>Validates Bearer token from Authorization header</li>
     *   <li>Enforces role-based access control</li>
     *   <li>Forwards user email and role as request headers</li>
     * </ol>
     *
     * @param exchange the current server web exchange
     * @param chain    the gateway filter chain
     * @return {@link Mono} completing when the filter chain completes
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();

        log.debug("Incoming request: {} {}", method, path);

        if (isPublicGetRoute(path, method)) {
            log.debug("Public GET route allowed: {}", path);
            return chain.filter(exchange);
        }

        if (isAuthRoute(path)) {
            log.debug("Auth route allowed without token: {}", path);
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders()
                .getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header " +
                    "for path: {}", path);
            return unauthorizedResponse(exchange,
                    "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.isTokenValid(token)) {
            log.warn("Invalid or expired token for path: {}", path);
            return unauthorizedResponse(exchange,
                    "Invalid or expired token");
        }

        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            log.debug("Authenticated request - email: {}, role: {}, " +
                    "path: {}", email, role, path);

            if (isRestaurantOwnerRoute(path, method)) {
                if (!role.equals("RESTAURANT_OWNER")) {
                    log.warn("Access denied for role: {} on path: {}",
                            role, path);
                    return forbiddenResponse(exchange,
                            "Access denied! Only RESTAURANT_OWNER " +
                                    "can perform this action.");
                }
            }

            if (path.startsWith("/api/orders") &&
                    method.equals("POST")) {
                if (!role.equals("CUSTOMER")) {
                    log.warn("Access denied - only CUSTOMER can " +
                            "place orders, role: {}", role);
                    return forbiddenResponse(exchange,
                            "Access denied! Only CUSTOMER can place orders.");
                }
            }

            if (path.startsWith("/api/delivery") &&
                    method.equals("PUT")) {
                if (!role.equals("AGENT")) {
                    log.warn("Access denied - only AGENT can update " +
                            "delivery status, role: {}", role);
                    return forbiddenResponse(exchange,
                            "Access denied! Only AGENT can update " +
                                    "delivery status.");
                }
            }

            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Email", email)
                    .header("X-User-Role", role)
                    .build();

            log.debug("Forwarding request to service - " +
                    "email: {}, role: {}", email, role);

            return chain.filter(
                    exchange.mutate()
                            .request(modifiedRequest)
                            .build());

        } catch (Exception e) {
            log.error("Token processing failed for path: {} - {}",
                    path, e.getMessage());
            return unauthorizedResponse(exchange,
                    "Token processing failed");
        }
    }

    /**
     * Checks if the request is a public GET route
     * that does not require authentication.
     * Customers and guests can view menus and restaurants
     * without logging in.
     *
     * @param path   the request URL path
     * @param method the HTTP method of the request
     * @return {@code true} if the route is a public GET route,
     *         {@code false} otherwise
     */
    private boolean isPublicGetRoute(String path, String method) {
        if (!method.equals("GET")) return false;
        return path.equals("/api/menu/available") ||
                path.startsWith("/api/restaurants") ||
                path.startsWith("/api/menu/restaurant/");
    }

    /**
     * Checks if the request path is an authentication route.
     * Auth routes are always public regardless of HTTP method.
     *
     * @param path the request URL path
     * @return {@code true} if the path is an auth route,
     *         {@code false} otherwise
     */
    private boolean isAuthRoute(String path) {
        return path.equals("/auth/register") ||
                path.equals("/auth/login");
    }

    /**
     * Checks if the request requires RESTAURANT_OWNER role.
     * Write operations (POST, PUT, DELETE) on restaurant
     * and menu endpoints are restricted to restaurant owners.
     *
     * @param path   the request URL path
     * @param method the HTTP method of the request
     * @return {@code true} if the route requires RESTAURANT_OWNER role,
     *         {@code false} otherwise
     */
    private boolean isRestaurantOwnerRoute(String path, String method) {
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

    /**
     * Builds and returns a {@code 401 Unauthorized} response.
     * Called when the JWT token is missing, invalid or expired.
     *
     * @param exchange the current server web exchange
     * @param message  the error message to include in the response body
     * @return {@link Mono} that writes the unauthorized response
     */
    private Mono<Void> unauthorizedResponse(
            ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        var buffer = response.bufferFactory()
                .wrap(("{\"error\": \"" + message + "\"}").getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * Builds and returns a {@code 403 Forbidden} response.
     * Called when the user's role does not have permission
     * to access the requested route.
     *
     * @param exchange the current server web exchange
     * @param message  the error message to include in the response body
     * @return {@link Mono} that writes the forbidden response
     */
    private Mono<Void> forbiddenResponse(
            ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().add("Content-Type", "application/json");
        var buffer = response.bufferFactory()
                .wrap(("{\"error\": \"" + message + "\"}").getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * Returns the order of this filter in the filter chain.
     * Value of {@code -1} ensures this filter runs before
     * all other Gateway filters.
     *
     * @return the filter order value
     */
    @Override
    public int getOrder() {
        return -1;
    }
}