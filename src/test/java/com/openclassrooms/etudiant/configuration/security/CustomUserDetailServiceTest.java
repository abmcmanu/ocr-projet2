package com.openclassrooms.etudiant.configuration.security;

import com.openclassrooms.etudiant.entities.User;
import com.openclassrooms.etudiant.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailService service;

    @BeforeEach
    void setUp() {
        service = new CustomUserDetailService(userRepository);
    }

    @Test
    void loadUserByUsername_userFound_returnsUserDetails() {
        User user = new User();
        user.setLogin("john");
        user.setPassword("encoded");
        when(userRepository.findByLogin("john")).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("john");

        assertThat(result.getUsername()).isEqualTo("john");
    }

    @Test
    void loadUserByUsername_userNotFound_throwsUsernameNotFoundException() {
        when(userRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("unknown");
    }
}