package com.huit._theway.service;

import com.huit._theway.model.Product;
import com.huit._theway.model.Review;
import com.huit._theway.model.User;
import com.huit._theway.model.Order;
import com.huit._theway.model.OrderItem;
import com.huit._theway.repository.ProductRepository;
import com.huit._theway.repository.ReviewRepository;
import com.huit._theway.repository.UserRepository;
import com.huit._theway.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public List<Review> getReviewsByProduct(Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            return reviewRepository.findByProductOrderByCreatedAtDesc(product);
        }
        return new ArrayList<>();
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public boolean canUserReview(String username, Long productId) {
        try {
            if (username == null || productId == null) return false;
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) return false;

            // Lấy đơn hàng kèm sản phẩm một cách an toàn
            List<Order> orders = orderRepository.findByUserWithItems(user);
            if (orders == null || orders.isEmpty()) return false;

            // Duyệt tìm sản phẩm trong các đơn hàng đã hoàn thành (COMPLETED)
            return orders.stream()
                    .filter(o -> "COMPLETED".equals(o.getStatus()))
                    .filter(o -> o.getItems() != null)
                    .flatMap(o -> o.getItems().stream())
                    .anyMatch(item -> item.getProduct() != null && item.getProduct().getId().equals(productId));
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public void saveReview(Long productId, String username, String content, Integer rating) {
        if (!canUserReview(username, productId)) {
            return; // Ngăn chặn lưu nếu chưa mua hàng hoặc đơn chưa hoàn thành
        }

        Product product = productRepository.findById(productId).orElse(null);
        User user = userRepository.findByUsername(username).orElse(null);

        if (product != null && user != null) {
            Review review = Review.builder()
                    .product(product)
                    .user(user)
                    .customerName(user.getFullName() != null && !user.getFullName().isEmpty() ? user.getFullName() : user.getUsername())
                    .content(content)
                    .rating(rating)
                    .build();
            reviewRepository.save(review);
        }
    }

    @Transactional
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
