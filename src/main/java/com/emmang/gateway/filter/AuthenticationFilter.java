package com.emmang.gateway.filter;

import com.emmang.gateway.constant.ExceptionMessage;
import com.emmang.gateway.exception.InvalidTokenException;
import com.emmang.gateway.exception.MissingAuthorizationException;
import com.emmang.gateway.util.JWTUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator routeValidator;

    private final JWTUtil jwtUtil;

    public AuthenticationFilter(RouteValidator routeValidator, JWTUtil jwtUtil) {
        super(Config.class);
        this.routeValidator = routeValidator;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (routeValidator.isSecured.test(request)) {
                String authHeader = request.getHeaders().getFirst("Authorization");
                String token = "";

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                } else {
                    throw new MissingAuthorizationException(ExceptionMessage.MISSING_AUTHORIZATION);
                }

                try {
                    if (!jwtUtil.isTokenValid(token)) {
                        throw new InvalidTokenException(ExceptionMessage.INVALID_TOKEN);
                    }
                    return chain.filter(populateRequestWithHeaders(exchange, jwtUtil.extractAllClaims(token)));
                } catch (Exception e) {
                    throw new InvalidTokenException(ExceptionMessage.INVALID_TOKEN);
                }
            }
            return chain.filter(exchange);
        });
    }

    private ServerWebExchange populateRequestWithHeaders(ServerWebExchange exchange, Claims claims) {
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("username",String.valueOf(claims.get("username")))
                .header("role", String.valueOf(claims.get("role")))
                .build();

        return exchange.mutate().request(mutatedRequest).build();
    }

    public static class Config {}

}
