package com.huit._theway.controller;

import com.huit._theway.model.Category;
import com.huit._theway.model.Product;
import com.huit._theway.model.Order;
import com.huit._theway.model.Review;
import com.huit._theway.model.SiteSetting;
import com.huit._theway.model.User;
import com.huit._theway.service.CategoryService;
import com.huit._theway.service.OrderService;
import com.huit._theway.service.ProductService;
import com.huit._theway.service.ReviewService;
import com.huit._theway.service.SiteSettingService;
import com.huit._theway.service.UserService;
import com.huit._theway.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final OrderService orderService;
    private final ReviewService reviewService;
    private final SiteSettingService siteSettingService;
    private final AuditLogService auditLogService;

    @GetMapping("")
    public String dashboard(Model model) {
        try {
            List<Order> allOrders = orderService.getAllOrders();
            if (allOrders == null) allOrders = new ArrayList<>();

            long pCount = 0;
            try { pCount = productService.searchProducts("").size(); } catch (Exception e) {}
            
            long uCount = 0;
            try { uCount = userService.getAllUsers().size(); } catch (Exception e) {}

            long pendingCount = 0;
            long shippingCount = 0;
            long completedCount = 0;
            long cancelledCount = 0;
            double revenue = 0.0;
            
            for (Order o : allOrders) {
                if (o != null) {
                    if ("PENDING".equals(o.getStatus())) pendingCount++;
                    else if ("SHIPPING".equals(o.getStatus())) shippingCount++;
                    else if ("COMPLETED".equals(o.getStatus())) {
                        completedCount++;
                        if (o.getTotalAmount() != null) revenue += o.getTotalAmount();
                    }
                    else if ("CANCELLED".equals(o.getStatus())) cancelledCount++;
                }
            }

            model.addAttribute("productCount", pCount);
            model.addAttribute("orderCount", (long) allOrders.size());
            model.addAttribute("pendingCount", pendingCount);
            model.addAttribute("shippingCount", shippingCount);
            model.addAttribute("completedCount", completedCount);
            model.addAttribute("cancelledCount", cancelledCount);
            model.addAttribute("userCount", uCount);
            model.addAttribute("totalRevenue", revenue);
            model.addAttribute("orders", allOrders);
            model.addAttribute("lowStockProducts", productService.getLowStockProducts(5));
        } catch (Exception e) {
            model.addAttribute("productCount", 0L);
            model.addAttribute("orderCount", 0L);
            model.addAttribute("userCount", 0L);
            model.addAttribute("totalRevenue", 0.0);
            model.addAttribute("orders", new ArrayList<Order>());
        }
        return "admin/dashboard";
    }

    // --- QUẢN LÝ DANH MỤC ---
    @GetMapping("/categories")
    public String listCategories(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                 @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                 Model model) {
        org.springframework.data.domain.Page<Category> categoryPage = categoryService.searchAndPaginate(keyword, page - 1, 10);
        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "admin/categories/list";
    }

    @GetMapping("/categories/add")
    public String showAddCategory(Model model) {
        model.addAttribute("category", new Category());
        return "admin/categories/add";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute("category") Category category) {
        boolean isNew = (category.getId() == null);
        categoryService.saveCategory(category);
        auditLogService.logAction(isNew ? "CREATE" : "UPDATE", "Category", String.valueOf(category.getId()), "Tên danh mục: " + category.getName());
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String showEditCategory(@PathVariable("id") Long id, Model model) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) return "redirect:/admin/categories";
        model.addAttribute("category", category);
        return "admin/categories/edit";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        Category cat = categoryService.getCategoryById(id);
        if (cat != null) {
            categoryService.deleteCategory(id);
            auditLogService.logAction("DELETE", "Category", String.valueOf(id), "Tên danh mục: " + cat.getName());
        }
        return "redirect:/admin/categories";
    }

    // --- QUẢN LÝ SẢN PHẨM ---
    @GetMapping("/products")
    public String listProducts(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                               @RequestParam(value = "categoryId", required = false) Long categoryId,
                               @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                               Model model) {
        org.springframework.data.domain.Page<Product> productPage = productService.searchAndPaginateAdmin(keyword, categoryId, page - 1, 10);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        return "admin/products/list";
    }

    @GetMapping("/products/add")
    public String showAddProduct(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/products/add";
    }

    @PostMapping("/products/save")
    public String saveProduct(@jakarta.validation.Valid @ModelAttribute("product") Product product, 
                              org.springframework.validation.BindingResult result,
                              @RequestParam("category.id") Long categoryId,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("errorMsg", result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/products/add";
        }
        try {
            if (!imageFile.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path uploadPath = Paths.get("uploads");
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

                try (InputStream inputStream = imageFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    product.setMainImageUrl("/uploads/" + fileName);
                }
            } else if (product.getId() != null) {
                Product existing = productService.getProductById(product.getId());
                if (existing != null) product.setMainImageUrl(existing.getMainImageUrl());
            }

            Category cat = categoryService.getAllCategories().stream()
                    .filter(c -> c.getId().equals(categoryId))
                    .findFirst().orElse(null);
            product.setCategory(cat);
            boolean isNew = (product.getId() == null);
            productService.saveProduct(product);
            auditLogService.logAction(isNew ? "CREATE" : "UPDATE", "Product", String.valueOf(product.getId()), "Tên sản phẩm: " + product.getName());
            ra.addFlashAttribute("successMsg", "Lưu sản phẩm thành công!");
        } catch (IOException e) {
            ra.addFlashAttribute("errorMsg", "Lỗi tải ảnh: " + e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi hệ thống: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit/{id}")
    public String showEditProduct(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) return "redirect:/admin/products";
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/products/edit";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        Product p = productService.getProductById(id);
        if (p != null) {
            productService.deleteProduct(id);
            auditLogService.logAction("DELETE", "Product", String.valueOf(id), "Tên sản phẩm: " + p.getName());
        }
        return "redirect:/admin/products";
    }

    // --- QUẢN LÝ NGƯỜI DÙNG ---
    @GetMapping("/users")
    public String listUsers(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                            Model model) {
        org.springframework.data.domain.Page<User> userPage = userService.searchAndPaginate(keyword, page - 1, 10);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "admin/users/list";
    }

    @GetMapping("/users/add")
    public String showAddUser(Model model) {
        model.addAttribute("user", new User());
        return "admin/users/add";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute("user") User user, 
                           @RequestParam("role") String role,
                           org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        if (userService.existsByUsername(user.getUsername())) {
            ra.addFlashAttribute("errorMsg", "Tên đăng nhập đã tồn tại!");
            return "redirect:/admin/users/add";
        }
        if (userService.existsByEmail(user.getEmail())) {
            ra.addFlashAttribute("errorMsg", "Email đã tồn tại!");
            return "redirect:/admin/users/add";
        }
        
        user.setRoles(java.util.Collections.singleton(role));
        user.setEnabled(true);
        User savedUser = userService.registerUser(user);
        auditLogService.logAction("CREATE", "User", String.valueOf(savedUser.getId()), "Tạo tài khoản: " + savedUser.getUsername() + " (" + role + ")");
        ra.addFlashAttribute("successMsg", "Thêm người dùng thành công!");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/toggle-status")
    public String toggleUserStatus(@RequestParam("userId") Long userId, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        userService.toggleUserStatus(userId);
        auditLogService.logAction("TOGGLE_STATUS", "User", String.valueOf(userId), "Thay đổi trạng thái tài khoản");
        ra.addFlashAttribute("successMsg", "Cập nhật trạng thái người dùng thành công!");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/change-password")
    public String changeUserPassword(@RequestParam("userId") Long userId, 
                                     @RequestParam("newPassword") String newPassword, 
                                     org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        userService.adminChangePassword(userId, newPassword);
        auditLogService.logAction("CHANGE_PASSWORD", "User", String.valueOf(userId), "Admin đổi mật khẩu cho User ID: " + userId);
        ra.addFlashAttribute("successMsg", "Đổi mật khẩu thành công!");
        return "redirect:/admin/users";
    }

    // --- QUẢN LÝ ĐƠN HÀNG ---
    @GetMapping("/orders")
    public String listOrders(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                             @RequestParam(value = "status", required = false, defaultValue = "") String status,
                             @RequestParam(value = "date", required = false, defaultValue = "") String date,
                             @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                             Model model) {
        org.springframework.data.domain.Page<Order> orderPage = orderService.searchAndPaginate(keyword, status, date, page - 1, 10);
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("date", date);
        return "admin/orders/list";
    }

    @PostMapping("/orders/update-status")
    public String updateOrderStatus(@RequestParam("orderId") Long orderId, 
                                    @RequestParam("status") String status) {
        orderService.updateOrderStatus(orderId, status);
        auditLogService.logAction("UPDATE_STATUS", "Order", String.valueOf(orderId), "Cập nhật trạng thái thành: " + status);
        return "redirect:/admin/orders";
    }

    // --- QUẢN LÝ ĐÁNH GIÁ ---
    @GetMapping("/reviews")
    public String listReviews(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                              @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                              Model model) {
        org.springframework.data.domain.Page<Review> reviewPage = reviewService.searchAndPaginate(keyword, page - 1, 10);
        model.addAttribute("reviews", reviewPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reviewPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "admin/reviews/list";
    }

    @GetMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable("id") Long id) {
        reviewService.deleteReview(id);
        auditLogService.logAction("DELETE", "Review", String.valueOf(id), "Xóa đánh giá");
        return "redirect:/admin/reviews";
    }

    // --- NHẬT KÝ HOẠT ĐỘNG ---
    @GetMapping("/audit-logs")
    public String listAuditLogs(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                @RequestParam(value = "date", required = false, defaultValue = "") String date,
                                @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                Model model) {
        if (date == null || date.isEmpty()) {
            date = java.time.LocalDate.now().toString();
        }
        org.springframework.data.domain.Page<com.huit._theway.model.AuditLog> logPage = auditLogService.searchAndPaginate(keyword, date, page - 1, 20);
        model.addAttribute("logs", logPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", logPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("date", date);
        return "admin/audit-logs/list";
    }

    // --- CÀI ĐẶT TRANG CHỦ ---
    @GetMapping("/settings/home")
    public String showHomeSettings(Model model) {
        model.addAttribute("settings", siteSettingService.getSettings());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/settings/home";
    }

    @PostMapping("/settings/home/save")
    public String saveHomeSettings(@ModelAttribute("settings") SiteSetting settings,
                                   @RequestParam("image1File") MultipartFile image1File,
                                   @RequestParam("image2File") MultipartFile image2File,
                                   org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        try {
            SiteSetting existing = siteSettingService.getSettings();
            
            // Handle image 1 upload
            if (!image1File.isEmpty()) {
                String fileName1 = UUID.randomUUID().toString() + "_" + image1File.getOriginalFilename();
                Path uploadPath = Paths.get("uploads");
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                try (InputStream inputStream = image1File.getInputStream()) {
                    Files.copy(inputStream, uploadPath.resolve(fileName1), StandardCopyOption.REPLACE_EXISTING);
                    settings.setSlide1Image("/uploads/" + fileName1);
                }
            } else {
                settings.setSlide1Image(existing.getSlide1Image());
            }

            // Handle image 2 upload
            if (!image2File.isEmpty()) {
                String fileName2 = UUID.randomUUID().toString() + "_" + image2File.getOriginalFilename();
                Path uploadPath = Paths.get("uploads");
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                try (InputStream inputStream = image2File.getInputStream()) {
                    Files.copy(inputStream, uploadPath.resolve(fileName2), StandardCopyOption.REPLACE_EXISTING);
                    settings.setSlide2Image("/uploads/" + fileName2);
                }
            } else {
                settings.setSlide2Image(existing.getSlide2Image());
            }
            
            // Set category titles implicitly
            Category cat1 = categoryService.getCategoryBySlug(settings.getCategory1Slug());
            if (cat1 != null) settings.setCategory1Title(cat1.getName().toUpperCase());
            
            Category cat2 = categoryService.getCategoryBySlug(settings.getCategory2Slug());
            if (cat2 != null) settings.setCategory2Title(cat2.getName().toUpperCase());

            siteSettingService.saveSettings(settings);
            ra.addFlashAttribute("successMsg", "Cập nhật cài đặt trang chủ thành công!");
        } catch (IOException e) {
            ra.addFlashAttribute("errorMsg", "Lỗi tải ảnh: " + e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi hệ thống: " + e.getMessage());
        }
        return "redirect:/admin/settings/home";
    }
}
