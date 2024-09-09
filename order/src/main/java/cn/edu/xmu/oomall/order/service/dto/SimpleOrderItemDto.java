package cn.edu.xmu.oomall.order.service.dto;

import lombok.Data;

@Data
public class SimpleOrderItemDto {
    private Long productId;
    private Long orderId;
    private String name;
    private Integer quantity;
    private Integer price;
    private Integer discountPrice;
}
