package com.vivaeventos.authservice.service;

import com.vivaeventos.authservice.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    // 50 chars = 400 bits, suficiente para HS256 (mínimo 256 bits)
    private static final String TEST_SECRET = "test-secret-key-for-jwt-unit-tests-only-1234567890";
    private static final long TEST_EXPIRATION_MS = 86400000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMs", TEST_EXPIRATION_MS);
    }

    @Test
    void generateToken_retornaTokenNoVacio() {
        String token = jwtService.generateToken("test@example.com", "ROLE_USER");
        assertThat(token).isNotBlank();
    }

    @Test
    void extractEmail_tokenValido_retornaEmailCorrecto() {
        String token = jwtService.generateToken("test@example.com", "ROLE_USER");
        assertThat(jwtService.extractEmail(token)).isEqualTo("test@example.com");
    }

    @Test
    void isTokenValid_tokenValido_retornaTrue() {
        String token = jwtService.generateToken("test@example.com", "ROLE_USER");
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void isTokenValid_tokenExpirado_retornaFalse() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L);
        String token = jwtService.generateToken("test@example.com", "ROLE_USER");
        assertThat(jwtService.isTokenValid(token)).isFalse();
    }
}
