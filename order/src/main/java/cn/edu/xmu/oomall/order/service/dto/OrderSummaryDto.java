package cn.edu.xmu.oomall.order.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class OrderSummaryDto {
    private Long id;
    private Integer status;
    private LocalDateTime gmtCreate;
    private Long originPrice;
    private Long discountPrice;
    private Long expressFee;
}
