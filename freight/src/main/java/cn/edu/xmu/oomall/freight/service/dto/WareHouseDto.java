package cn.edu.xmu.oomall.freight.service.dto;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ClassName WareHouseDto
 * Description  TODO
 *
 * @author Mr_X
 * @version 1.0
 * @date 2022/12/4 12:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WareHouseDto {
    private Long id;
    private String address;
    private String name;
    private String senderName;
    private String senderMobile;
    private Integer status;
    private Integer priority;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private UserDto creator;
    private UserDto modifier;
    @JsonAlias("region")
    private RegionDto region;
}

