package com.vivaeventos.authservice.service;

import com.vivaeventos.authservice.dto.AuthResponse;
import com.vivaeventos.authservice.dto.LoginRequest;
import com.vivaeventos.authservice.dto.RegisterRequest;
import com.vivaeventos.authservice.exception.EmailAlreadyExistsException;
import com.vivaeventos.authservice.model.User;
import com.vivaeventos.authservice.repository.UserRepository;
import com.vivaeventos.authservice.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    // --- register ---

    @Test
    void register_emailValido_guardaUsuarioConRoleUser() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("nuevo@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("nuevo@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");

        authService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo("nuevo@example.com");
        assertThat(saved.getPassword()).isEqualTo("hashed");
        assertThat(saved.getRole()).isEqualTo("ROLE_USER");
        assertThat(saved.isEnabled()).isTrue();
    }

    @Test
    void register_emailDuplicado_lanzaEmailAlreadyExistsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existente@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("existente@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("El email ya está registrado");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_contrasenaHasheada_noSeGuardaEnPlano() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("plainPassword");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("$2a$10$hashedValue");

        authService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isNotEqualTo("plainPassword");
    }

    // --- login ---

    @Test
    void login_credencialesCorrectas_retornaToken() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");

        User user = User.builder()
                .email("user@example.com")
                .password("hashed")
                .role("ROLE_USER")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("user@example.com", "ROLE_USER")).thenReturn("mocked-jwt");

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("mocked-jwt");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_credencialesIncorrectas_lanzaBadCredentialsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("wrongpassword");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);

        verify(userRepository, never()).findByEmail(anyString());
    }
}
