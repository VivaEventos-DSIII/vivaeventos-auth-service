package com.vivaeventos.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para la validación del JWT.
 *
 * El API Gateway llama a /auth/validate con el JWT.
 * Si es válido, el auth-service devuelve este DTO con el email y rol
 * para que el gateway los inyecte en los headers de la petición.
 *
 * @Data          → genera getters, setters, equals, hashCode y toString.
 * @AllArgsConstructor → constructor con todos los campos.
 * @NoArgsConstructor  → constructor vacío requerido por Jackson.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateResponse {

    /**
     * Email del usuario autenticado extraído del JWT.
     * Los microservicios lo reciben en el header X-User-Email.
     */
    private String email;

    /**
     * Rol del usuario (ROLE_USER o ROLE_ADMIN).
     * El gateway lo inyecta en el header X-User-Role para que
     * cada microservicio pueda aplicar su propia lógica de autorización.
     */
    private String role;
}
