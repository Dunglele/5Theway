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

    @GetMapping("")
    public String dashboard(Model model) {
        try {
            List<Order> allOrders = orderService.getAllOrders();
            if (allOrders == null) allOrders = new ArrayList<>();

            long pCount = 0;
            try { pCount = productService.searchProducts("").size(); } catch (Exception e) {}
            
            long uCount = 0;
            try { uCount = userService.getAllUsers().size(); } catch (Exception e) {}

            double revenue = 0.0;
            for (Order o : allOrders) {
                if (o != null && "COMPLETED".equals(o.getStatus()) && o.getTotalAmount() != null) {
                    revenue += o.getTotalAmount();
                }
            }

            model.addAttribute("productCount", pCount);
            model.addAttribute("orderCount", (long) allOrders.size());
            model.addAttribute("userCount", uCount);
            model.addAttribute("totalRevenue", revenue);
            model.addAttribute("orders", allOrders);
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
        categoryService.saveCategory(category);
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
        categoryService.deleteCategory(id);
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
    public String saveProduct(@ModelAttribute("product") Product product, 
                              @RequestParam("category.id") Long categoryId,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
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
            productService.saveProduct(product);
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
        productService.deleteProduct(id);
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

    @GetMapping("/users/toggle/{id}")
    public String toggleUserStatus(@PathVariable("id") Long id) {
        userService.toggleUserStatus(id);
        return "redirect:/admin/users";
    }

    // --- QUẢN LÝ ĐƠN HÀNG ---
    @GetMapping("/orders")
    public String listOrders(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                             @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                             Model model) {
        org.springframework.data.domain.Page<Order> orderPage = orderService.searchAndPaginate(keyword, page - 1, 10);
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "admin/orders/list";
    }

    @PostMapping("/orders/update-status")
    public String updateOrderStatus(@RequestParam("orderId") Long orderId, 
                                    @RequestParam("status") String status) {
        orderService.updateOrderStatus(orderId, status);
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
        return "redirect:/admin/reviews";
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
