package cn.edu.xmu.oomall.freight.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseRegionUpdateVo {
    @NotNull(message = "开始时间不为空")
    private String beginTime;
    @NotNull(message = "结束时间不为空")
    private String endTime;
}
