package com.huit._theway.controller;

import com.huit._theway.model.Coupon;
import com.huit._theway.service.CouponService;
import com.huit._theway.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/coupons")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminCouponController {

    private final CouponService couponService;
    private final AuditLogService auditLogService;

    @GetMapping("")
    public String listCoupons(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                              @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                              Model model) {
        Page<Coupon> couponPage = couponService.searchAndPaginate(keyword, page - 1, 10);
        model.addAttribute("coupons", couponPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", couponPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "admin/coupons/list";
    }

    @GetMapping("/add")
    public String addCouponForm(Model model) {
        model.addAttribute("coupon", new Coupon());
        return "admin/coupons/add";
    }

    @PostMapping("/save")
    public String saveCoupon(@ModelAttribute("coupon") Coupon coupon, RedirectAttributes ra) {
        boolean isNew = (coupon.getId() == null);
        couponService.saveCoupon(coupon);
        auditLogService.logAction(isNew ? "CREATE_COUPON" : "UPDATE_COUPON", "Coupon", String.valueOf(coupon.getId()), 
            (isNew ? "Tạo mới" : "Cập nhật") + " mã giảm giá: " + coupon.getCode());
        ra.addFlashAttribute("successMsg", (isNew ? "Thêm mới" : "Cập nhật") + " mã giảm giá thành công!");
        return "redirect:/admin/coupons";
    }

    @GetMapping("/edit/{id}")
    public String editCouponForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Coupon coupon = couponService.getCouponById(id);
        if (coupon == null) {
            ra.addFlashAttribute("errorMsg", "Không tìm thấy mã giảm giá.");
            return "redirect:/admin/coupons";
        }
        model.addAttribute("coupon", coupon);
        return "admin/coupons/edit";
    }

    @GetMapping("/delete/{id}")
    public String deleteCoupon(@PathVariable("id") Long id, RedirectAttributes ra) {
        Coupon coupon = couponService.getCouponById(id);
        if (coupon != null) {
            couponService.deleteCoupon(id);
            auditLogService.logAction("DELETE_COUPON", "Coupon", String.valueOf(id), "Xóa mã giảm giá: " + coupon.getCode());
            ra.addFlashAttribute("successMsg", "Xóa mã giảm giá thành công!");
        }
        return "redirect:/admin/coupons";
    }
}
