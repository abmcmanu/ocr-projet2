package com.openclassrooms.etudiant.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;

class RestExceptionHandlerTest {

    private RestExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new RestExceptionHandler();
        webRequest = new ServletWebRequest(new MockHttpServletRequest());
    }

    @Test
    void handleConflict_illegalArgumentException_returns400() {
        ResponseEntity<Object> response = handler.handleConflict(
                new IllegalArgumentException("invalid"), webRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleConflict_illegalStateException_returns400() {
        ResponseEntity<Object> response = handler.handleConflict(
                new IllegalStateException("bad state"), webRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleBadCredentials_returns401() {
        ResponseEntity<Object> response = handler.handleBadCredentialsException(
                new BadCredentialsException("wrong credentials"), webRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void handleForbidden_returns403() {
        ResponseEntity<Object> response = handler.handleForbiddenException(
                new AccessDeniedException("access denied"), webRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void handleDataIntegrityViolation_returns409() {
        ResponseEntity<Object> response = handler.handleDataIntegrityViolation(
                new DataIntegrityViolationException("duplicate entry"), webRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void handleException_returns500() {
        ResponseEntity<Object> response = handler.handleException(
                new RuntimeException("unexpected"), webRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}