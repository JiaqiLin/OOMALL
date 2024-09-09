package cn.edu.xmu.oomall.freight.service.dto;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UndeliverableDto {
    private LocalDateTime beginTime;//开始时间
    private LocalDateTime endTime;//结束时间
    private UserDto creator;//创建者
    private UserDto modifier;//修改者
    private LocalDateTime gmtCreate;//创建时间
    private LocalDateTime gmtModified;//修改时间
    @JsonAlias("region")
    private RegionDto regionDto;//地区
}
