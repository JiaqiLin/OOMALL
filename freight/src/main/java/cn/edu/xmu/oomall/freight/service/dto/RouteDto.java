package cn.edu.xmu.oomall.freight.service.dto;

import lombok.Data;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression.*;

import java.time.LocalDateTime;

@Data
public class RouteDto {
//    Route:
//    type: "object"
//    properties:
//    content:
//    type: "string"
//    description: "内容"
//    gmtCreate:
//    type: "string"
//    format: "datetime"
//    description: "创建时间"
    private String content;
    private LocalDateTime gmtCreate;
}
