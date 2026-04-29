package com.huit._theway.service;

import com.huit._theway.model.Coupon;
import com.huit._theway.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public Coupon validateCoupon(String code, Double orderAmount) {
        Optional<Coupon> couponOpt = couponRepository.findByCodeAndActiveTrue(code);
        
        if (couponOpt.isPresent()) {
            Coupon coupon = couponOpt.get();
            if (!coupon.isExpired() && orderAmount >= coupon.getMinOrderAmount()) {
                return coupon;
            }
        }
        return null;
    }

    public void saveCoupon(Coupon coupon) {
        couponRepository.save(coupon);
    }
    
    public java.util.List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id).orElse(null);
    }

    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }

    public org.springframework.data.domain.Page<Coupon> searchAndPaginate(String keyword, int page, int size) {
        java.util.List<Coupon> all = couponRepository.findAll();
        String lowerKeyword = keyword != null ? keyword.toLowerCase() : "";
        java.util.List<Coupon> filtered = all.stream()
                .filter(c -> c.getCode().toLowerCase().contains(lowerKeyword))
                .collect(java.util.stream.Collectors.toList());

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        java.util.List<Coupon> pageContent = (start <= end && start < filtered.size()) ? filtered.subList(start, end) : new java.util.ArrayList<>();

        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, filtered.size());
    }
}
