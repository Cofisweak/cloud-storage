package com.cofisweak.cloudstorage.service;

import com.cofisweak.cloudstorage.domain.User;
import com.cofisweak.cloudstorage.domain.exception.PasswordsNotEqualsException;
import com.cofisweak.cloudstorage.domain.exception.UsernameAlreadyExistsException;
import com.cofisweak.cloudstorage.repository.UserRepository;
import com.cofisweak.cloudstorage.web.dto.RegisterDto;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
public class UserServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @DynamicPropertySource
    static void registerPostgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
    }

    @After
    public void resetDatabase() {
        userRepository.deleteAll();
    }

    @Test
    public void whenRegisterUserThenUserCreates() {
        String username = "username";
        String password = "password";
        RegisterDto dto = new RegisterDto(username, password, password);
        userService.register(dto);

        Optional<User> user = userRepository.findByUsername(username);
        assertThat(user).isPresent();
        assertThat(user).get()
                .extracting(User::getUsername, User::getPassword)
                .containsExactly(username, passwordEncoder.encode(password));
    }

    @Test
    public void whenRegisterUserWithUniqueUsernameThenThrowsException() {
        String username = "username";
        String password = "password";
        RegisterDto dto = new RegisterDto(username, password, password);
        userService.register(dto);
        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(UsernameAlreadyExistsException.class);
    }

    @Test
    public void whenRegisterUserWithDifferentPasswordsThenThrowsException() {
        String username = "username";
        String password = "password";
        RegisterDto dto = new RegisterDto(username, password, password);
        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(PasswordsNotEqualsException.class);
    }

    @Test
    public void whenGettingNonExistingUserThenThrowsException() {
        String username = "username";
        assertThatThrownBy(() -> userService.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
