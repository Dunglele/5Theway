package com.huit._theway.service;

import com.huit._theway.model.User;
import com.huit._theway.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testRegisterUser() {
        // Given
        User user = User.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .fullName("Test User")
                .build();

        // When
        User registeredUser = userService.registerUser(user);

        // Then
        assertThat(registeredUser.getId()).isNotNull();
        assertThat(registeredUser.getUsername()).isEqualTo("testuser");
        // Kiểm tra mật khẩu đã được mã hóa
        assertThat(passwordEncoder.matches("password123", registeredUser.getPassword())).isTrue();
        // Kiểm tra vai trò mặc định
        assertThat(registeredUser.getRoles()).contains("ROLE_USER");
    }
}
