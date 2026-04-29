package com.huit._theway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Cấu hình bảo mật Spring Security
 * Mỗi thay đổi đều phải được chú thích bằng tiếng Việt
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/Home/**", "/assets/**", "/api/products/**", "/login", "/register", "/403", "/error", "/favicon.ico").permitAll()
                .requestMatchers("/admin/users/**", "/admin/settings/**", "/admin/audit-logs/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("5TheWaySecretKey123!") // Khóa bảo mật để mã hóa token
                .rememberMeParameter("remember-me") // Tên tham số từ checkbox
                .tokenValiditySeconds(86400 * 14) // Thời gian nhớ (14 ngày)
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/403") // Trang báo lỗi khi không có quyền truy cập
            );

        return http.build();
    }
}
