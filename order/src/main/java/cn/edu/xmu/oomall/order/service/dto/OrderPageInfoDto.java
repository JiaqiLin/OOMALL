package cn.edu.xmu.oomall.order.service.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName OrderPageInfoDto
 * Description  TODO
 *
 * @author Mr_X
 * @version 1.0
 * @date 2022/12/20 23:12
 */
@Data
@Builder
public class OrderPageInfoDto implements Serializable {
    private Long id;
    private Integer status;
    private String gmtCreate;
    private Integer originPrice;
    private Integer discountPrice;
    private Integer freightPrice;
}
