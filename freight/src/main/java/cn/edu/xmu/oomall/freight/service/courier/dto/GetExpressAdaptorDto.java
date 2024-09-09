package cn.edu.xmu.oomall.freight.service.courier.dto;

import cn.edu.xmu.oomall.freight.service.dto.RouteDto;
import lombok.Data;

import java.util.List;

@Data
public class GetExpressAdaptorDto {
    /**
     * 路由信息
     */
    List<RouteDto> routes;
    /**
     * 运单状态
     */
    Byte status;
}
