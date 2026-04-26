package com.huit._theway.controller;

import com.huit._theway.model.Product;
import com.huit._theway.service.CategoryService;
import com.huit._theway.service.OrderService;
import com.huit._theway.service.ProductService;
import com.huit._theway.service.ReviewService;
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

    @GetMapping("/")
    public String Index(Model model){
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("topProducts", productService.getProductsByCategory("tops"));
        
        List<Product> accessories = new ArrayList<>();
        accessories.addAll(productService.getProductsByCategory("accessories"));
        accessories.addAll(productService.getProductsByCategory("masks"));
        accessories.addAll(productService.getProductsByCategory("bags"));
        accessories.addAll(productService.getProductsByCategory("stickers"));
        
        model.addAttribute("allAccessoryProducts", accessories);
        return "home/index";
    }

    private void addFilterAttributes(Model model, String slug, Double minPrice, Double maxPrice, String sort) {
        model.addAttribute("products", productService.getProductsByCategoryFiltered(slug, minPrice, maxPrice, sort));
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sort", sort);
    }

    @GetMapping("/Home/Tops")
    public String Tops(@RequestParam(required = false) Double minPrice,
                       @RequestParam(required = false) Double maxPrice,
                       @RequestParam(required = false) String sort, Model model){
        addFilterAttributes(model, "tops", minPrice, maxPrice, sort);
        return "home/tops";
    }

    @GetMapping("/Home/Hoodies")
    public String Hoodies(@RequestParam(required = false) Double minPrice,
                          @RequestParam(required = false) Double maxPrice,
                          @RequestParam(required = false) String sort, Model model){
        addFilterAttributes(model, "hoodies", minPrice, maxPrice, sort);
        return "home/hoodies";
    }

    @GetMapping("/Home/Jackets")
    public String Jackets(@RequestParam(required = false) Double minPrice,
                          @RequestParam(required = false) Double maxPrice,
                          @RequestParam(required = false) String sort, Model model){
        addFilterAttributes(model, "jackets", minPrice, maxPrice, sort);
        return "home/jackets";
    }

    @GetMapping("/Home/Accessories")
    public String Accessories(@RequestParam(required = false) Double minPrice,
                              @RequestParam(required = false) Double maxPrice,
                              @RequestParam(required = false) String sort, Model model){
        addFilterAttributes(model, "accessories", minPrice, maxPrice, sort);
        return "home/accessories";
    }

    @GetMapping("/Home/Outlet")
    public String Outlet(){
        return "home/outlet";
    }

    @GetMapping("/Home/News")
    public String News(){
        return "home/news";
    }

    @GetMapping("/Home/Product/{id}")
    public String ProductDetail(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/";
        }
        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", productService.getProductsByCategory(product.getCategory().getSlug()));
        model.addAttribute("reviews", reviewService.getReviewsByProduct(id));
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
    public String Search(@RequestParam("keyword") String keyword, Model model) {
        model.addAttribute("keyword", keyword);
        model.addAttribute("products", productService.searchProducts(keyword));
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
            return "home/profile";
        }
        return "redirect:/login";
    }
}
