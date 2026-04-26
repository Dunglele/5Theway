package com.huit._theway.service;

import com.huit._theway.model.Product;
import com.huit._theway.model.Review;
import com.huit._theway.model.User;
import com.huit._theway.repository.ProductRepository;
import com.huit._theway.repository.ReviewRepository;
import com.huit._theway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<Review> getReviewsByProduct(Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            return reviewRepository.findByProductOrderByCreatedAtDesc(product);
        }
        return List.of();
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Transactional
    public void saveReview(Long productId, String username, String content, Integer rating) {
        Product product = productRepository.findById(productId).orElse(null);
        User user = userRepository.findByUsername(username).orElse(null);

        if (product != null && user != null) {
            Review review = Review.builder()
                    .product(product)
                    .user(user)
                    .customerName(user.getFullName())
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
