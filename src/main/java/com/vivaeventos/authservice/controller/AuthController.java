package com.vivaeventos.authservice.controller;

import com.vivaeventos.authservice.dto.AuthResponse;
import com.vivaeventos.authservice.dto.LoginRequest;
import com.vivaeventos.authservice.dto.MessageResponse;
import com.vivaeventos.authservice.dto.RegisterRequest;
import com.vivaeventos.authservice.service.AuthService;
import com.vivaeventos.authservice.dto.ValidateResponse;
import com.vivaeventos.authservice.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Usuario registrado exitosamente"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    /**
     * GET /auth/validate
     *
     * Valida el JWT enviado en el header Authorization
     * Este endpoint es llamado exclusivamente por el API Gateway
     * antes de enrutar cada petición a los microservicios.
     *
     * Criterio 1: sin JWT → Spring Security retorna 401 automáticamente
     *             antes de llegar a este método (SecurityConfig lo controla).
     * Criterio 2: JWT inválido o expirado → JwtAuthenticationFilter del
     *             auth-service lo intercepta y retorna 401.
     * Criterio 3: JWT válido → retorna 200 con email y rol del usuario
     *             para que el gateway los inyecte en los headers.
     *
     * @param authHeader Header Authorization con el JWT (Bearer <token>)
     * @return ValidateResponse con email y rol si el token es válido
     */
    @GetMapping("/validate")
    public ResponseEntity<ValidateResponse> validate(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

        // Extraer el token quitando el prefijo "Bearer "
        String token = authHeader.substring(7);

        // Extraer email y rol del token (ya validado por JwtAuthenticationFilter)
        String email = jwtService.extractEmail(token);
        String role  = jwtService.extractRole(token);

        return ResponseEntity.ok(new ValidateResponse(email, role));
    }
    private final JwtService jwtService;
}
