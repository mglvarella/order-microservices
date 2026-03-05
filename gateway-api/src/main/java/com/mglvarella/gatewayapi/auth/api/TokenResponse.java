package com.mglvarella.gatewayapi.auth.api;

public record TokenResponse(
        String accessToken,
        String tokenType,
        Long expiresIn
) {
    public TokenResponse(String accessToken, Long duration) {
        this(accessToken, "Bearer", duration);
    }
}