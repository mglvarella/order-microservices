package com.mglvarella.gatewayapi.auth.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        Field secretField = JwtService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtService, "testetecnicocotefacil2026secrets");
    }

    private Authentication createAuthentication(String username, String role) {
        return new UsernamePasswordAuthenticationToken(
                username, null,
                List.of(new SimpleGrantedAuthority(role)));
    }

    @Test
    void generateToken_shouldReturnNonNullToken() {
        Authentication auth = createAuthentication("usuario", "USER");

        String token = jwtService.generateToken(auth);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void isTokenValid_shouldReturnTrue_forValidToken() {
        Authentication auth = createAuthentication("usuario", "USER");
        String token = jwtService.generateToken(auth);

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_shouldReturnFalse_forTamperedToken() {
        Authentication auth = createAuthentication("usuario", "USER");
        String token = jwtService.generateToken(auth);

        assertFalse(jwtService.isTokenValid(token + "tampered"));
    }

    @Test
    void isTokenValid_shouldReturnFalse_forGarbageToken() {
        assertFalse(jwtService.isTokenValid("not.a.valid.jwt"));
    }

    @Test
    void extractUsername_shouldReturnCorrectSubject() {
        Authentication auth = createAuthentication("usuario", "USER");
        String token = jwtService.generateToken(auth);

        String username = jwtService.extractUsername(token);

        assertEquals("usuario", username);
    }
}
