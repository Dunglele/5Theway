package com.huit._theway.controller;

import com.huit._theway.model.Product;
import com.huit._theway.model.User;
import com.huit._theway.model.SiteSetting;
import com.huit._theway.repository.UserRepository;
import com.huit._theway.service.CategoryService;
import com.huit._theway.service.OrderService;
import com.huit._theway.service.ProductService;
import com.huit._theway.service.ReviewService;
import com.huit._theway.service.SiteSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final SiteSettingService siteSettingService;

    @GetMapping("/")
    public String Index(Model model){
        SiteSetting settings = siteSettingService.getSettings();
        model.addAttribute("settings", settings);
        model.addAttribute("categories", categoryService.getAllCategories());
        
        model.addAttribute("category1Products", productService.getProductsByCategory(settings.getCategory1Slug()));
        model.addAttribute("category2Products", productService.getProductsByCategory(settings.getCategory2Slug()));
        
        return "home/index";
    }

    private void addFilterAttributes(Model model, String slug, Double minPrice, Double maxPrice, String sort, String color, String status, int page) {
        org.springframework.data.domain.Page<Product> productPage = productService.getProductsByCategoryFiltered(slug, minPrice, maxPrice, sort, color, status, page, 18);
        model.addAttribute("category", categoryService.getCategoryBySlug(slug));
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sort", sort);
        model.addAttribute("color", color);
        model.addAttribute("status", status);
    }

    @GetMapping("/Home/Tops")
    public String Tops(@RequestParam(required = false) Double minPrice,
                       @RequestParam(required = false) Double maxPrice,
                       @RequestParam(required = false) String sort,
                       @RequestParam(required = false) String color,
                       @RequestParam(required = false) String status, 
                       @RequestParam(defaultValue = "0") int page, Model model){
        addFilterAttributes(model, "tops", minPrice, maxPrice, sort, color, status, page);
        return "home/tops";
    }

    @GetMapping("/Home/Hoodies")
    public String Hoodies(@RequestParam(required = false) Double minPrice,
                          @RequestParam(required = false) Double maxPrice,
                          @RequestParam(required = false) String sort,
                          @RequestParam(required = false) String color,
                          @RequestParam(required = false) String status, 
                          @RequestParam(defaultValue = "0") int page, Model model){
        addFilterAttributes(model, "hoodies", minPrice, maxPrice, sort, color, status, page);
        return "home/hoodies";
    }

    @GetMapping("/Home/Jackets")
    public String Jackets(@RequestParam(required = false) Double minPrice,
                          @RequestParam(required = false) Double maxPrice,
                          @RequestParam(required = false) String sort,
                          @RequestParam(required = false) String color,
                          @RequestParam(required = false) String status, 
                          @RequestParam(defaultValue = "0") int page, Model model){
        addFilterAttributes(model, "jackets", minPrice, maxPrice, sort, color, status, page);
        return "home/jackets";
    }

    @GetMapping("/Home/Accessories")
    public String Accessories(@RequestParam(required = false) Double minPrice,
                              @RequestParam(required = false) Double maxPrice,
                              @RequestParam(required = false) String sort,
                              @RequestParam(required = false) String color,
                              @RequestParam(required = false) String status, 
                              @RequestParam(defaultValue = "0") int page, Model model){
        addFilterAttributes(model, "accessories", minPrice, maxPrice, sort, color, status, page);
        return "home/accessories";
    }

    @GetMapping("/Home/Outlet")
    public String Outlet(@RequestParam(required = false) Double minPrice,
                         @RequestParam(required = false) Double maxPrice,
                         @RequestParam(required = false) String sort,
                         @RequestParam(required = false) String color,
                         @RequestParam(required = false) String status, 
                         @RequestParam(defaultValue = "0") int page, Model model){
        model.addAttribute("categories", categoryService.getAllCategories());
        org.springframework.data.domain.Page<Product> productPage = productService.getProductsByCategoryFiltered(null, minPrice, maxPrice, sort, color, status, page, 18);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sort", sort);
        model.addAttribute("color", color);
        model.addAttribute("status", status);
        return "home/outlet";
    }

    @GetMapping("/Home/News")
    public String News(){
        return "home/news";
    }

    @GetMapping("/Home/Product/{id}")
    public String ProductDetail(@PathVariable("id") Long id, Model model, org.springframework.security.core.Authentication auth) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/";
        }
        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", productService.getProductsByCategory(product.getCategory().getSlug()));
        model.addAttribute("reviews", reviewService.getReviewsByProduct(id));
        
        // Kiểm tra quyền đánh giá
        boolean canReview = false;
        if (auth != null && auth.isAuthenticated()) {
            canReview = reviewService.canUserReview(auth.getName(), id);
        }
        model.addAttribute("canReview", canReview);
        
        return "home/product-detail";
    }

    @PostMapping("/Home/Product/Review")
    public String submitReview(@RequestParam("productId") Long productId,
                               @RequestParam("content") String content,
                               @RequestParam("rating") Integer rating,
                               org.springframework.security.core.Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            reviewService.saveReview(productId, auth.getName(), content, rating);
        }
        return "redirect:/Home/Product/" + productId;
    }

    @GetMapping("/Home/Search")
    public String Search(@RequestParam("keyword") String keyword,
                         @RequestParam(required = false) Double minPrice,
                         @RequestParam(required = false) Double maxPrice,
                         @RequestParam(required = false) String sort,
                         @RequestParam(required = false) String color,
                         @RequestParam(required = false) String status, 
                         @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("keyword", keyword);
        model.addAttribute("categories", categoryService.getAllCategories());
        org.springframework.data.domain.Page<Product> productPage = productService.searchProductsFiltered(keyword, minPrice, maxPrice, sort, color, status, page, 18);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sort", sort);
        model.addAttribute("color", color);
        model.addAttribute("status", status);
        return "home/search-results";
    }

    @GetMapping("/login")
    public String login() {
        return "home/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new com.huit._theway.dto.UserRegistrationDto());
        return "home/register";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "home/403";
    }

    @GetMapping("/Home/Orders")
    public String userOrders(org.springframework.security.core.Authentication auth, Model model) {
        if (auth != null && auth.isAuthenticated()) {
            model.addAttribute("orders", orderService.getOrdersByUser(auth.getName()));
            return "home/orders";
        }
        return "redirect:/login";
    }

    @GetMapping("/Home/Profile")
    public String userProfile(org.springframework.security.core.Authentication auth, Model model) {
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
            return "home/profile";
        }
        return "redirect:/login";
    }
}
