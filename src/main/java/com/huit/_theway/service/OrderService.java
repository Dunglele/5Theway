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

        if ("COD".equals(order.getPaymentMethod())) {
            order.setStatus("CONFIRMED"); // Đơn tiền mặt tự động xác nhận
        } else {
            order.setStatus("PENDING"); // Đơn chuyển khoản chờ duyệt tiền
        }
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

    @Transactional
    public boolean cancelOrder(Long orderId, String username) {
        Order order = orderRepository.findById(orderId).orElse(null);
        // Chỉ cho phép hủy nếu đơn hàng thuộc về user đó và trạng thái là PENDING
        if (order != null && order.getUser().getUsername().equals(username) && "PENDING".equals(order.getStatus())) {
            order.setStatus("CANCELLED");
            
            // Hoàn lại tồn kho
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    Product product = item.getProduct();
                    if (product != null) {
                        product.setStock(product.getStock() + item.getQuantity());
                        productRepository.save(product);
                    }
                }
            }
            
            orderRepository.save(order);
            return true;
        }
        return false;
    }

    public org.springframework.data.domain.Page<Order> searchAndPaginate(String keyword, String status, String date, int page, int size) {
        List<Order> all = orderRepository.findAll();
        String lowerKeyword = keyword != null ? keyword.toLowerCase() : "";
        
        java.time.LocalDate filterDate = null;
        if (date != null && !date.trim().isEmpty()) {
            try {
                filterDate = java.time.LocalDate.parse(date);
            } catch (Exception e) {
                // Ignore parse error
            }
        }
        final java.time.LocalDate finalFilterDate = filterDate;
        
        List<Order> filtered = all.stream()
                .filter(o -> {
                    boolean matchKeyword = lowerKeyword.isEmpty() ||
                            o.getId().toString().contains(lowerKeyword) || 
                            (o.getFullName() != null && o.getFullName().toLowerCase().contains(lowerKeyword)) ||
                            (o.getPhoneNumber() != null && o.getPhoneNumber().contains(lowerKeyword));
                    boolean matchStatus = status == null || status.isEmpty() || status.equals(o.getStatus());
                    boolean matchDate = finalFilterDate == null || (o.getCreatedAt() != null && o.getCreatedAt().toLocalDate().equals(finalFilterDate));
                    
                    return matchKeyword && matchStatus && matchDate;
                })
                .collect(java.util.stream.Collectors.toList());

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<Order> pageContent = (start <= end && start < filtered.size()) ? filtered.subList(start, end) : new java.util.ArrayList<>();

        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, filtered.size());
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
            // Nếu đã Hủy hoặc Hoàn thành thì không cho phép đổi nữa
            if ("CANCELLED".equals(order.getStatus()) || "COMPLETED".equals(order.getStatus())) {
                return;
            }

            // Nếu trạng thái mới là CANCELLED -> Hoàn lại tồn kho
            if ("CANCELLED".equals(status)) {
                if (order.getItems() != null) {
                    for (OrderItem item : order.getItems()) {
                        Product product = item.getProduct();
                        if (product != null) {
                            product.setStock(product.getStock() + item.getQuantity());
                            productRepository.save(product);
                        }
                    }
                }
            }

            order.setStatus(status);
            orderRepository.save(order);
        }
    }

    public List<Order> getOrdersByUser(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            return orderRepository.findByUserWithItems(user);
        }
        return new ArrayList<>();
    }
}
