package cn.edu.xmu.oomall.freight.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseRegionVo {
    @NotNull(message = "商户id不为空")
    private Integer shopId;
    @NotNull(message = "地区id不为空")
    private Long id;
    @NotNull(message = "仓库id不为空")
    private Integer wid;
    @NotNull(message = "开始时间不为空")
    private String beginTime;
    @NotNull(message = "结束时间不为空")
    private String endTime;
    @NotNull(message = "优先级不为空")
    private Long priority;
}
