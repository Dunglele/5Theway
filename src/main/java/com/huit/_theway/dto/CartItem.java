package com.huit._theway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO đại diện cho một sản phẩm trong giỏ hàng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long productId;
    private String name;
    private String imageUrl;
    private Double price;
    private Integer quantity;

    public Double getTotalPrice() {
        return price * quantity;
    }
}
