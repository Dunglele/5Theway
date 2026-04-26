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
}
