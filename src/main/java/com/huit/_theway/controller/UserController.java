package com.huit._theway.controller;

import com.huit._theway.dto.UserRegistrationDto;
import com.huit._theway.model.User;
import com.huit._theway.service.UserService;
import com.huit._theway.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller xử lý các chức năng người dùng và đơn hàng cá nhân
 */
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OrderService orderService;

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") UserRegistrationDto registrationDto, 
                               RedirectAttributes redirectAttributes) {
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

    @PostMapping("/Home/Profile/Update")
    public String updateProfile(@RequestParam("fullName") String fullName,
                                @RequestParam("phoneNumber") String phoneNumber,
                                @RequestParam("address") String address,
                                org.springframework.security.core.Authentication auth,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserProfile(auth.getName(), fullName, phoneNumber, address);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cập nhật thất bại: " + e.getMessage());
        }
        return "redirect:/Home/Profile";
    }

    @PostMapping("/Home/Profile/ChangePassword")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 org.springframework.security.core.Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới xác nhận không khớp!");
            return "redirect:/Home/Profile";
        }
        boolean success = userService.changePassword(auth.getName(), oldPassword, newPassword);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu cũ không chính xác!");
        }
        return "redirect:/Home/Profile";
    }

    @PostMapping("/Home/Orders/Cancel/{id}")
    public String cancelOrder(@PathVariable("id") Long id,
                              org.springframework.security.core.Authentication auth,
                              RedirectAttributes redirectAttributes) {
        boolean success = orderService.cancelOrder(id, auth.getName());
        if (success) {
            redirectAttributes.addFlashAttribute("success", "Đã hủy đơn hàng thành công và hoàn lại tồn kho.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không thể hủy đơn hàng này. Chỉ đơn hàng đang chờ xác nhận mới có thể hủy.");
        }
        return "redirect:/Home/Orders";
    }
}
