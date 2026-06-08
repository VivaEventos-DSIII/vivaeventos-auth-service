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
    private final JwtService jwtService;

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

    @GetMapping("/validate")
    public ResponseEntity<ValidateResponse> validate(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        String role  = jwtService.extractRole(token);
        return ResponseEntity.ok(new ValidateResponse(email, role));
    }
}
