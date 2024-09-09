package cn.edu.xmu.oomall.freight.controller.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class WarehouseLogisticsVo {
    @NotNull(message = "商铺id不可为空")
    private Long shopId;
    @NotNull(message = "仓库id不可为空")
    private Long Id;
    @NotNull(message = "仓库物流id不可为空")
    private Long lid;
    @NotNull(message = "仓库物流信息不可为空")
    private WarehouseLogisticsInfoVo warehouseLogisticsInfoVo;
}
