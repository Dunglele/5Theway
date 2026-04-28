package com.huit._theway.service;

import com.huit._theway.dto.CartItem;
import com.huit._theway.model.Product;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service quản lý giỏ hàng sử dụng Session
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final ProductService productService;
    private static final String CART_SESSION_KEY = "shoppingCart";

    @SuppressWarnings("unchecked")
    public Map<Long, CartItem> getCart(HttpSession session) {
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute(CART_SESSION_KEY, cart);
            log.info("Created new cart in session: {}", session.getId());
        }
        return cart;
    }

    public void addToCart(Long productId, Integer quantity, String color, String size, HttpSession session) {
        log.info("Adding product {} (color: {}, size: {}) with quantity {} to cart.", productId, color, size, quantity);
        Map<Long, CartItem> cart = getCart(session);
        Product product = productService.getProductById(productId);
        
        if (product != null) {
            Double activePrice = (product.getSalePrice() != null) ? product.getSalePrice() : product.getPrice();
            
            // Để đơn giản, ta vẫn dùng productId làm key. 
            // Nếu muốn hỗ trợ cùng SP nhưng khác màu/size là item riêng, cần đổi key thành "id_color_size"
            if (cart.containsKey(productId)) {
                CartItem item = cart.get(productId);
                item.setQuantity(item.getQuantity() + quantity);
                item.setColor(color);
                item.setSize(size);
            } else {
                CartItem item = CartItem.builder()
                        .productId(productId)
                        .name(product.getName())
                        .imageUrl(product.getMainImageUrl())
                        .price(activePrice)
                        .quantity(quantity)
                        .color(color)
                        .size(size)
                        .build();
                cart.put(productId, item);
            }
            session.setAttribute(CART_SESSION_KEY, cart);
        }
    }

    public void updateQuantity(Long productId, Integer quantity, HttpSession session) {
        Map<Long, CartItem> cart = getCart(session);
        if (cart.containsKey(productId)) {
            if (quantity <= 0) {
                cart.remove(productId);
            } else {
                cart.get(productId).setQuantity(quantity);
            }
            session.setAttribute(CART_SESSION_KEY, cart);
        }
    }

    public void removeFromCart(Long productId, HttpSession session) {
        Map<Long, CartItem> cart = getCart(session);
        cart.remove(productId);
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }

    public Double getTotalAmount(HttpSession session) {
        return getCart(session).values().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }

    public Integer getCount(HttpSession session) {
        return getCart(session).values().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}
