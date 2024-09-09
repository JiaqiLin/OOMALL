package cn.edu.xmu.oomall.freight.service.dto;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetWareHouseDto {
    private ShopLogisticsDto shopLogisticsDto;
    private String beginTime;
    private String endTime;
    private Integer status;
    private UserDto creator;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private UserDto modifier;
}
