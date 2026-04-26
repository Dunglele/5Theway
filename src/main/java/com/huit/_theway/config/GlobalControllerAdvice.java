package com.huit._theway.config;

import com.huit._theway.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final CartService cartService;

    @ModelAttribute("cartCount")
    public Integer getCartCount(HttpSession session) {
        return cartService.getCount(session);
    }
}
