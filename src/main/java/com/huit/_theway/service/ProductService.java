package com.huit._theway.service;

import com.huit._theway.model.Product;
import com.huit._theway.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> getFeaturedProducts(int limit) {
        return productRepository.findByFeaturedTrue(PageRequest.of(0, limit));
    }

    public List<Product> getNewArrivals(int limit) {
        return productRepository.findByNewArrivalTrue(PageRequest.of(0, limit));
    }

    public List<Product> getSaleProducts(int limit) {
        return productRepository.findBySalePriceNotNull(PageRequest.of(0, limit));
    }

    public List<Product> getProductsByCategory(String slug) {
        return productRepository.findByCategorySlug(slug);
    }

    public org.springframework.data.domain.Page<Product> searchAndPaginateAdmin(String keyword, Long categoryId, int page, int size) {
        List<Product> all = productRepository.findAll();
        String lowerKeyword = keyword != null ? keyword.toLowerCase() : "";
        List<Product> filtered = all.stream()
                .filter(p -> p.getId().toString().contains(lowerKeyword) || (p.getName() != null && p.getName().toLowerCase().contains(lowerKeyword)))
                .filter(p -> categoryId == null || (p.getCategory() != null && p.getCategory().getId().equals(categoryId)))
                .collect(java.util.stream.Collectors.toList());

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<Product> pageContent = (start <= end && start < filtered.size()) ? filtered.subList(start, end) : new java.util.ArrayList<>();

        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, filtered.size());
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }
    
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findByStockLessThan(threshold);
    }

    public org.springframework.data.domain.Page<Product> searchProductsFiltered(String keyword, Double minPrice, Double maxPrice, String sort, String color, String status, int page, int size) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
        List<Product> filtered = applyFilters(products, minPrice, maxPrice, sort, color, status);
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<Product> pageContent = (start <= end && start < filtered.size()) ? filtered.subList(start, end) : new java.util.ArrayList<>();
        
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, filtered.size());
    }

    private List<Product> applyFilters(List<Product> products, Double minPrice, Double maxPrice, String sort, String color, String status) {
        // 1. Lọc theo giá
        if (minPrice != null || maxPrice != null) {
            products = products.stream()
                .filter(p -> {
                    double price = (p.getSalePrice() != null) ? p.getSalePrice() : p.getPrice();
                    boolean ok = true;
                    if (minPrice != null) ok = price >= minPrice;
                    if (ok && maxPrice != null) ok = price <= maxPrice;
                    return ok;
                })
                .collect(java.util.stream.Collectors.toList());
        }

        // 2. Lọc theo màu sắc
        if (color != null && !color.isEmpty()) {
            products = products.stream()
                .filter(p -> p.getColor() != null && p.getColor().equalsIgnoreCase(color))
                .collect(java.util.stream.Collectors.toList());
        }

        // 3. Lọc theo trạng thái kho
        if (status != null && !status.isEmpty()) {
            if (status.equalsIgnoreCase("inStock")) {
                products = products.stream().filter(p -> p.getStock() > 0).collect(java.util.stream.Collectors.toList());
            } else if (status.equalsIgnoreCase("lowStock")) {
                products = products.stream().filter(p -> p.getStock() > 0 && p.getStock() < 15).collect(java.util.stream.Collectors.toList());
            }
        }

        // 4. Sắp xếp
        if (sort != null) {
            switch (sort) {
                case "priceAsc":
                    products.sort((p1, p2) -> {
                        double price1 = (p1.getSalePrice() != null) ? p1.getSalePrice() : p1.getPrice();
                        double price2 = (p2.getSalePrice() != null) ? p2.getSalePrice() : p2.getPrice();
                        return Double.compare(price1, price2);
                    });
                    break;
                case "priceDesc":
                    products.sort((p1, p2) -> {
                        double price1 = (p1.getSalePrice() != null) ? p1.getSalePrice() : p1.getPrice();
                        double price2 = (p2.getSalePrice() != null) ? p2.getSalePrice() : p2.getPrice();
                        return Double.compare(price2, price1);
                    });
                    break;
                case "newest":
                    products.sort((p1, p2) -> {
                        if (p1.getCreatedAt() == null || p2.getCreatedAt() == null) return 0;
                        return p2.getCreatedAt().compareTo(p1.getCreatedAt());
                    });
                    break;
            }
        }
        return products;
    }

    public org.springframework.data.domain.Page<Product> getProductsByCategoryFiltered(String slug, Double minPrice, Double maxPrice, String sort, String color, String status, int page, int size) {
        List<Product> products;
        if (slug == null || slug.isEmpty() || slug.equalsIgnoreCase("all")) {
            products = productRepository.findAll();
        } else {
            products = productRepository.findByCategorySlug(slug);
        }
        List<Product> filtered = applyFilters(new java.util.ArrayList<>(products), minPrice, maxPrice, sort, color, status);
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<Product> pageContent = (start <= end && start < filtered.size()) ? filtered.subList(start, end) : new java.util.ArrayList<>();
        
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, filtered.size());
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> getRandomProducts(int limit) {
        List<Product> all = productRepository.findAll();
        java.util.Collections.shuffle(all);
        return all.stream().limit(limit).collect(java.util.stream.Collectors.toList());
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
