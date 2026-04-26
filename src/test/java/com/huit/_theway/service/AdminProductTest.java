package com.huit._theway.service;

import com.huit._theway.model.Category;
import com.huit._theway.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class AdminProductTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Test
    public void testAddAndDeleteProduct() {
        // 1. Chuẩn bị danh mục
        Category cat = Category.builder().name("Test Category").slug("test-cat").build();
        categoryService.saveCategory(cat);

        // 2. Test THÊM sản phẩm
        Product product = Product.builder()
                .name("Test Product")
                .price(100000.0)
                .stock(10)
                .mainImageUrl("test.jpg")
                .category(cat)
                .build();
        
        Product savedProduct = productService.saveProduct(product);
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Test Product");

        // 3. Test CẬP NHẬT sản phẩm
        savedProduct.setName("Updated Name");
        savedProduct.setPrice(200000.0);
        Product updatedProduct = productService.saveProduct(savedProduct);
        assertThat(updatedProduct.getName()).isEqualTo("Updated Name");
        assertThat(updatedProduct.getPrice()).isEqualTo(200000.0);

        // 4. Test XÓA sản phẩm
        Long id = updatedProduct.getId();
        productService.deleteProduct(id);
        
        Product deletedProduct = productService.getProductById(id);
        assertThat(deletedProduct).isNull();
    }
}
