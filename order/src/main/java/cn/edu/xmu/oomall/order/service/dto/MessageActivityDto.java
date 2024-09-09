package cn.edu.xmu.oomall.order.service.dto;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import lombok.Data;

@Data
public class MessageActivityDto {
    private Long id;
    private String name;
    private UserDto userDto;
}
