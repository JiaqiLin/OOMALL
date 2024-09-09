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
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Builder
public class ShopLogisticsDto {
    private Long id;
    @JsonAlias("logistics")
    private LogisticsDto logistics;
    private Byte invalid;
    private String secret;
    private Integer priority;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private UserDto creator;
    private UserDto modifier;
}
