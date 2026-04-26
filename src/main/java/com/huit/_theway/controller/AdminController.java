package com.huit._theway.controller;

import com.huit._theway.model.Category;
import com.huit._theway.model.Product;
import com.huit._theway.service.CategoryService;
import com.huit._theway.service.OrderService;
import com.huit._theway.service.ProductService;
import com.huit._theway.service.ReviewService;
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

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final OrderService orderService;
    private final ReviewService reviewService;

    @GetMapping("")
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productService.searchProducts("").size());
        model.addAttribute("totalOrders", orderService.getAllOrders().size());
        double revenue = orderService.getAllOrders().stream()
                .filter(o -> "COMPLETED".equals(o.getStatus()))
                .mapToDouble(com.huit._theway.model.Order::getTotalAmount)
                .sum();
        model.addAttribute("totalRevenue", revenue);
        return "admin/dashboard";
    }

    // --- QUẢN LÝ DANH MỤC ---
    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
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
    public String listProducts(Model model) {
        model.addAttribute("products", productService.searchProducts("")); 
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
            // Xử lý Upload Ảnh
            if (!imageFile.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path uploadPath = Paths.get("uploads");
                
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = imageFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    // Lưu đường dẫn URL để hiển thị (ví dụ: /uploads/abc.jpg)
                    product.setMainImageUrl("/uploads/" + fileName);
                }
            } else if (product.getId() != null) {
                // Nếu là update và không chọn ảnh mới, giữ nguyên ảnh cũ
                Product existing = productService.getProductById(product.getId());
                if (existing != null) {
                    product.setMainImageUrl(existing.getMainImageUrl());
                }
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
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users/list";
    }

    @GetMapping("/users/toggle/{id}")
    public String toggleUserStatus(@PathVariable("id") Long id) {
        userService.toggleUserStatus(id);
        return "redirect:/admin/users";
    }

    // --- QUẢN LÝ ĐƠN HÀNG ---
    @GetMapping("/orders")
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
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
    public String listReviews(Model model) {
        model.addAttribute("reviews", reviewService.getAllReviews());
        return "admin/reviews/list";
    }

    @GetMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable("id") Long id) {
        reviewService.deleteReview(id);
        return "redirect:/admin/reviews";
    }
}
