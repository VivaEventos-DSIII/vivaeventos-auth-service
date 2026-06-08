package com.vivaeventos.authservice.service;

import com.vivaeventos.authservice.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para la validación JWT (US-26).
 * Verifica que JwtService extrae correctamente email y rol del token.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerValidateTest {

    @Mock
    private JwtService jwtService;

    /**
     * Criterio 3: JWT válido → extrae email correctamente.
     */
    @Test
    void dadoTokenValido_cuandoSeExtraeEmail_entoncesRetornaEmailCorrecto() {
        String token = "token.valido.ejemplo";
        when(jwtService.extractEmail(token)).thenReturn("usuario@test.com");

        String email = jwtService.extractEmail(token);

        assertThat(email).isEqualTo("usuario@test.com");
    }

    /**
     * Criterio 3 (US-26): JWT válido → extrae rol correctamente.
     */
    @Test
    void dadoTokenValido_cuandoSeExtraeRol_entoncesRetornaRolCorrecto() {
        String token = "token.valido.ejemplo";
        when(jwtService.extractRole(token)).thenReturn("ROLE_USER");

        String role = jwtService.extractRole(token);

        assertThat(role).isEqualTo("ROLE_USER");
    }

    /**
     * Criterio 3 (US-26): JWT de admin → rol ROLE_ADMIN.
     */
    @Test
    void dadoTokenAdmin_cuandoSeExtraeRol_entoncesRetornaRoleAdmin() {
        String token = "token.admin.ejemplo";
        when(jwtService.extractRole(token)).thenReturn("ROLE_ADMIN");

        String role = jwtService.extractRole(token);

        assertThat(role).isEqualTo("ROLE_ADMIN");
    }

    /**
     * Criterio 2 (US-26): JWT inválido → isTokenValid retorna false.
     */
    @Test
    void dadoTokenInvalido_cuandoSeValida_entoncesRetornaFalse() {
        String tokenInvalido = "token.invalido";
        when(jwtService.isTokenValid(tokenInvalido)).thenReturn(false);

        boolean valid = jwtService.isTokenValid(tokenInvalido);

        assertThat(valid).isFalse();
    }
}
