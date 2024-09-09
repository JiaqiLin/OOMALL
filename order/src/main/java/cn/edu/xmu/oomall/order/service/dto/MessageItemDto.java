package cn.edu.xmu.oomall.order.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageItemDto {
    private Long id;
    private String name;
    private Long couponId;
    private Long onsaleId;
    private Integer quantity;
    private Long price;
    private Long discount;
    private Long activityId;
}
