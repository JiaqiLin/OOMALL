package cn.edu.xmu.oomall.order.service.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName CouponActivityDto
 * Description  TODO
 *
 * @author Mr_X
 * @version 1.0
 * @date 2022/12/20 23:43
 */
@Builder
@Data
public class CouponActivityDto implements Serializable {
    private Long id;
    private String name;
    private Integer quantity;
    private String couponTime;
}
