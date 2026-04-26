package com.huit._theway.controller;

import com.huit._theway.dto.UserRegistrationDto;
import com.huit._theway.model.User;
import com.huit._theway.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller xử lý các chức năng đăng ký, thông tin người dùng
 * Mỗi thay đổi đều phải được chú thích bằng tiếng Việt
 */
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Hiển thị trang đăng ký đã được định nghĩa trong HomeController, 
    // nhưng ta có thể chuyển logic sang đây để tập trung quản lý User.
    // Tạm thời ta sẽ xử lý PostMapping đăng ký tại đây.

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") UserRegistrationDto registrationDto, 
                               RedirectAttributes redirectAttributes) {
        
        // Kiểm tra mật khẩu khớp nhau
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "redirect:/register";
        }

        try {
            User user = User.builder()
                    .username(registrationDto.getUsername())
                    .password(registrationDto.getPassword())
                    .email(registrationDto.getEmail())
                    .fullName(registrationDto.getFullName())
                    .build();
            
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("success", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đăng ký thất bại: " + e.getMessage());
            return "redirect:/register";
        }
    }
}
