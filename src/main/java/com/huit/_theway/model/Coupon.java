package com.huit._theway.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity đại diện cho Mã giảm giá
 */
@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private Double discountValue; // Số tiền hoặc % giảm

    private String discountType; // PERCENTAGE hoặc FIXED_AMOUNT

    private Double minOrderAmount; // Đơn hàng tối thiểu để áp dụng

    private LocalDateTime expiryDate;

    private boolean active = true;

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDateTime.now());
    }
}
