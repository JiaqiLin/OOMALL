package cn.edu.xmu.oomall.order.dao.openfeign.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ItemDto {//订单商品的订货详情
    private Long orderItemId;
    private Long productId;
    private Integer quantity;
    private Integer weight;
}
