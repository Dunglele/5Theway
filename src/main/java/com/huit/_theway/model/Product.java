package com.huit._theway.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

/**
 * Entity đại diện cho Sản phẩm
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String shortDescription; // Mô tả ngắn cho trang chi tiết

    @Column(nullable = false)
    @Min(value = 0, message = "Giá sản phẩm không được nhỏ hơn 0")
    private Double price;

    private Double salePrice; // Giá khuyến mãi

    @Column(nullable = false)
    @Min(value = 0, message = "Số lượng tồn kho không được nhỏ hơn 0")
    private Integer stock; // Số lượng tồn kho

    private String color; // Màu sắc sản phẩm
    private String sizes; // Các kích thước sẵn có (ví dụ: "S,M,L,XL")

    private String mainImageUrl; // Ảnh chính
    private String additionalImages; // Danh sách ảnh phụ, phân cách bởi dấu phẩy

    private boolean featured = false; // Sản phẩm nổi bật (hiển thị trang chủ)
    private boolean newArrival = true; // Sản phẩm mới nhất

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
