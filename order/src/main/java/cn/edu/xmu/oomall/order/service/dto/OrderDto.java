package cn.edu.xmu.oomall.order.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.CouponDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName OrderDto
 * Description  TODO
 *
 * @author Mr_X
 * @version 1.0
 * @date 2022/12/20 23:49
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Builder
public class OrderDto implements Serializable {
    private Long id;
    private String orderSn;
    private CustomerDto customer;
    private ShopDto shop;
    private Long pId;
    private Integer status;
    private String gmtCreate;
    private String gmtModified;
    private Integer originPrice;
    private Integer discountPrice;
    private Integer expressFee;
    private String message;
    private ConsigneeDto consignee;
    private PackDto pack;
    private List<OrderItemsDto> orderItems;
}
