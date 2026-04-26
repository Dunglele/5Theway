package com.huit._theway.controller;

import com.huit._theway.dto.CartItem;
import com.huit._theway.model.Order;
import com.huit._theway.service.CartService;
import com.huit._theway.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/Home/Checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final com.huit._theway.service.CouponService couponService;

    @GetMapping("")
    public String showCheckout(HttpSession session, Model model) {
        Map<Long, CartItem> cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/Home/Cart";
        }
        
        Double totalAmount = cartService.getTotalAmount(session);
        
        model.addAttribute("order", new Order());
        model.addAttribute("cartItems", cart.values());
        model.addAttribute("totalAmount", totalAmount);
        
        // Kiểm tra xem đã có coupon áp dụng chưa
        com.huit._theway.model.Coupon appliedCoupon = (com.huit._theway.model.Coupon) session.getAttribute("appliedCoupon");
        double discount = 0.0;
        if (appliedCoupon != null) {
            if ("PERCENTAGE".equals(appliedCoupon.getDiscountType())) {
                discount = totalAmount * (appliedCoupon.getDiscountValue() / 100);
            } else {
                discount = appliedCoupon.getDiscountValue();
            }
        }
        model.addAttribute("discount", discount);
        
        return "home/checkout";
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
        return "redirect:/Home/Checkout";
    }

    @PostMapping("/PlaceOrder")
    public String placeOrder(@ModelAttribute("order") Order order, 
                             HttpSession session, 
                             Authentication authentication) {
        Map<Long, CartItem> cart = cartService.getCart(session);
        if (cart.isEmpty()) return "redirect:/";

        Double totalAmount = cartService.getTotalAmount(session);
        
        // Tính toán giảm giá từ session
        com.huit._theway.model.Coupon appliedCoupon = (com.huit._theway.model.Coupon) session.getAttribute("appliedCoupon");
        double discount = 0.0;
        if (appliedCoupon != null) {
            if ("PERCENTAGE".equals(appliedCoupon.getDiscountType())) {
                discount = totalAmount * (appliedCoupon.getDiscountValue() / 100);
            } else {
                discount = appliedCoupon.getDiscountValue();
            }
            session.removeAttribute("appliedCoupon"); // Xóa sau khi dùng
        }

        order.setTotalAmount(totalAmount - discount + (totalAmount >= 500000 ? 0 : 30000));
        String username = (authentication != null) ? authentication.getName() : null;
        
        Order savedOrder = orderService.createOrder(order, cart, username);
        cartService.clearCart(session);
        
        return "redirect:/Home/Checkout/Success?id=" + savedOrder.getId();
    }

    @GetMapping("/Success")
    public String orderSuccess(Long id, Model model) {
        model.addAttribute("orderId", id);
        return "home/order-success";
    }
}
