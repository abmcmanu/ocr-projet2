package com.openclassrooms.etudiant.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = "my-very-secret-key-that-is-long-enough-for-hs256-algorithm";
    private static final long EXPIRATION_MS = 86_400_000L;

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMs", EXPIRATION_MS);
        userDetails = User.withUsername("john").password("pass").authorities(List.of()).build();
    }

    @Test
    void generateToken_returnsNonNullJwtString() {
        String token = jwtService.generateToken(userDetails);
        assertThat(token).isNotNull().isNotEmpty().contains(".");
    }

    @Test
    void extractUsername_returnsUsernameFromToken() {
        String token = jwtService.generateToken(userDetails);
        assertThat(jwtService.extractUsername(token)).isEqualTo("john");
    }

    @Test
    void validateToken_validTokenAndMatchingUser_returnsTrue() {
        String token = jwtService.generateToken(userDetails);
        assertThat(jwtService.validateToken(token, userDetails)).isTrue();
    }

    @Test
    void validateToken_validTokenButDifferentUsername_returnsFalse() {
        String token = jwtService.generateToken(userDetails);
        UserDetails other = User.withUsername("other").password("pass").authorities(List.of()).build();
        assertThat(jwtService.validateToken(token, other)).isFalse();
    }
}