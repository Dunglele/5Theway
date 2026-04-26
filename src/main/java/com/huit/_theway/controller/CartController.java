package com.huit._theway.controller;

import com.huit._theway.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý các chức năng giỏ hàng
 */
@Controller
@RequestMapping("/Home/Cart")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class CartController {

    private final CartService cartService;

    @GetMapping("")
    public String viewCart(HttpSession session, Model model) {
        log.info("Viewing cart. Items count: {}", cartService.getCount(session));
        model.addAttribute("cartItems", cartService.getCart(session).values());
        model.addAttribute("totalAmount", cartService.getTotalAmount(session));
        return "home/cart";
    }

    @PostMapping("/Add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                            HttpSession session,
                            org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        log.info("POST /Home/Cart/Add received for productId: {}", productId);
        cartService.addToCart(productId, quantity, session);
        ra.addFlashAttribute("successMsg", "Đã thêm sản phẩm vào giỏ hàng!");
        return "redirect:/Home/Cart";
    }

    @GetMapping("/Remove/{id}")
    public String removeFromCart(@PathVariable("id") Long id, HttpSession session,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        cartService.removeFromCart(id, session);
        ra.addFlashAttribute("successMsg", "Đã xóa sản phẩm khỏi giỏ hàng.");
        return "redirect:/Home/Cart";
    }

    @PostMapping("/Update")
    public String updateQuantity(@RequestParam("productId") Long productId,
                                 @RequestParam("quantity") Integer quantity,
                                 HttpSession session) {
        cartService.updateQuantity(productId, quantity, session);
        return "redirect:/Home/Cart";
    }
}
