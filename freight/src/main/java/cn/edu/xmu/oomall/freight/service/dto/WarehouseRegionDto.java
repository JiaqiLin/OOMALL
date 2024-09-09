package cn.edu.xmu.oomall.freight.service.dto;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class WarehouseRegionDto {
    private RegionDto region;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private SimpleUserDto creator;
    private SimpleUserDto modifier;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
