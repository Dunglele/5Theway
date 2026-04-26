package com.huit._theway.service;

import com.huit._theway.dto.CartItem;
import com.huit._theway.model.Order;
import com.huit._theway.model.OrderItem;
import com.huit._theway.model.Product;
import com.huit._theway.model.User;
import com.huit._theway.repository.OrderItemRepository;
import com.huit._theway.repository.OrderRepository;
import com.huit._theway.repository.ProductRepository;
import com.huit._theway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Order createOrder(Order order, Map<Long, CartItem> cartItems, String username) {
        if (username != null) {
            User user = userRepository.findByUsername(username).orElse(null);
            order.setUser(user);
        }

        order.setStatus("PENDING");
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> items = new ArrayList<>();
        for (CartItem cartItem : cartItems.values()) {
            Product product = productRepository.findById(cartItem.getProductId()).orElse(null);
            if (product != null) {
                OrderItem item = OrderItem.builder()
                        .order(savedOrder)
                        .product(product)
                        .productName(cartItem.getName())
                        .price(cartItem.getPrice())
                        .quantity(cartItem.getQuantity())
                        .build();
                items.add(orderItemRepository.save(item));
                
                // Trừ tồn kho
                product.setStock(product.getStock() - cartItem.getQuantity());
                productRepository.save(product);
            }
        }
        savedOrder.setItems(items);
        return savedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
        }
    }

    public List<Order> getOrdersByUser(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            return orderRepository.findByUserOrderByCreatedAtDesc(user);
        }
        return new ArrayList<>();
    }
}
