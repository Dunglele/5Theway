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
    private final com.huit._theway.service.CouponService couponService;

    @GetMapping("")
    public String viewCart(HttpSession session, Model model) {
        log.info("Viewing cart. Items count: {}", cartService.getCount(session));
        model.addAttribute("cartItems", cartService.getCart(session).values());
        
        Double totalAmount = cartService.getTotalAmount(session);
        com.huit._theway.model.Coupon appliedCoupon = (com.huit._theway.model.Coupon) session.getAttribute("appliedCoupon");
        double discount = 0.0;
        if (appliedCoupon != null) {
            if ("PERCENTAGE".equals(appliedCoupon.getDiscountType())) {
                discount = totalAmount * (appliedCoupon.getDiscountValue() / 100);
            } else {
                discount = appliedCoupon.getDiscountValue();
            }
        }
        
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("discount", discount);
        return "home/cart";
    }

    @PostMapping("/ApplyCoupon")
    public String applyCoupon(@RequestParam("couponCode") String code, 
                               HttpSession session, 
                               org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        Double totalAmount = cartService.getTotalAmount(session);
        com.huit._theway.model.Coupon coupon = couponService.validateCoupon(code, totalAmount);
        
        if (coupon != null) {
            session.setAttribute("appliedCoupon", coupon);
            ra.addFlashAttribute("couponSuccess", "Áp dụng mã giảm giá thành công!");
        } else {
            ra.addFlashAttribute("couponError", "Mã giảm giá không hợp lệ hoặc không đủ điều kiện.");
        }
        return "redirect:/Home/Cart";
    }

    @PostMapping("/Add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                            @RequestParam(value = "color", required = false) String color,
                            @RequestParam(value = "size", required = false) String size,
                            HttpSession session,
                            org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        log.info("POST /Home/Cart/Add received for productId: {}, color: {}, size: {}", productId, color, size);
        cartService.addToCart(productId, quantity, color, size, session);
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
