package cn.edu.xmu.oomall.freight.service.dto;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ExpressDto {

    private Long id;
    private String billCode;
    private SimpleLogisticsDto logistics;
//    private List<RouteDto> routes;
    private ConsigneeDto shipper;
    private ConsigneeDto receiver;
    private Byte status;
    private SimpleUserDto creator;
    private SimpleUserDto modifier;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;


    @Builder
    public ExpressDto(Long id, String billCode, SimpleLogisticsDto logistics,  ConsigneeDto shipper, ConsigneeDto receiver, Byte status, SimpleUserDto creator, SimpleUserDto modifier, LocalDateTime gmtCreate, LocalDateTime gmtModified) {
        this.id = id;
        this.billCode = billCode;
        this.logistics = logistics;
//        this.routes = routes;
        this.shipper = shipper;
        this.receiver = receiver;
        this.status = status;
        this.creator = creator;
        this.modifier = modifier;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
    }
}
