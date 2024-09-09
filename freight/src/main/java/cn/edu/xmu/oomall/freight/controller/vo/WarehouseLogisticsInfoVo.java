package cn.edu.xmu.oomall.freight.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseLogisticsInfoVo {
    @NotNull(message="开始时间不为空")
    private String begintime;

    @NotNull(message="截止时间不为空")
    private String endtime;
}
