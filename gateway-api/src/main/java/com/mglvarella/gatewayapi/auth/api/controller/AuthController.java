package com.mglvarella.gatewayapi.auth.api.controller;

import com.mglvarella.gatewayapi.auth.api.dto.LoginRequest;
import com.mglvarella.gatewayapi.auth.api.dto.TokenResponse;
import com.mglvarella.gatewayapi.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(this.authService.login(loginRequest));
    }
}
