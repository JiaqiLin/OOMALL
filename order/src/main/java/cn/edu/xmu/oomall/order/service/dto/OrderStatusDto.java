package cn.edu.xmu.oomall.order.service.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName OrderStatusDto
 * Description  TODO
 *
 * @author Mr_X
 * @version 1.0
 * @date 2022/12/20 22:25
 */
@Data
@Builder
public class OrderStatusDto implements Serializable {
    private Integer code;
    private String name;
}
