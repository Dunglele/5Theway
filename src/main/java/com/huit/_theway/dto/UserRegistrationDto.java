package com.huit._theway.dto;

import lombok.Data;

/**
 * DTO cho việc đăng ký người dùng mới
 */
@Data
public class UserRegistrationDto {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String fullName;
}
