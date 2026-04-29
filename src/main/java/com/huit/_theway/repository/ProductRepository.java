package com.huit._theway.repository;

import com.huit._theway.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategorySlug(String slug);
    List<Product> findByFeaturedTrue(Pageable pageable);
    List<Product> findByNewArrivalTrue(Pageable pageable);
    List<Product> findBySalePriceNotNull(Pageable pageable);
    
    // Tìm kiếm theo tên (dùng cho tính năng Search)
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Tìm sản phẩm sắp hết hàng
    List<Product> findByStockLessThan(Integer stockThreshold);
}
