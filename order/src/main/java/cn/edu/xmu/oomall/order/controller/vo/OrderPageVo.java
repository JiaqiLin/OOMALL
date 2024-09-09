package cn.edu.xmu.oomall.order.controller.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * ClassName OrderPageVo
 * Description  TODO
 *
 * @author Mr_X
 * @version 1.0
 * @date 2022/12/20 23:59
 */
@Data
@NoArgsConstructor
public class OrderPageVo {
    private String orderSn;
    private Integer status;
    private String beginTime;
    private String endTime;

    @NotNull
    private Integer page;
    @NotNull
    private Integer pageSize;
}
