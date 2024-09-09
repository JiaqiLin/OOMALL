package cn.edu.xmu.oomall.freight.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopLogisticsVo {
    private Long logisticsId;
    @NotNull(message = "密钥不可为空")
    private String secret ;
    @NotNull(message = "优先级不可为空")
    private Integer priority;
}
