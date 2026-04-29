package com.huit._theway.controller;

import com.huit._theway.model.Product;
import com.huit._theway.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final ProductService productService;

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProductForQuickView(@PathVariable("id") Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", product.getId());
        response.put("name", product.getName());
        response.put("price", product.getPrice());
        response.put("salePrice", product.getSalePrice());
        response.put("stock", product.getStock());
        response.put("shortDescription", product.getShortDescription());
        response.put("mainImageUrl", product.getMainImageUrl());
        response.put("color", product.getColor());
        response.put("sizes", product.getSizes());
        if (product.getCategory() != null) {
            response.put("categoryName", product.getCategory().getName());
        }

        return ResponseEntity.ok(response);
    }
}
