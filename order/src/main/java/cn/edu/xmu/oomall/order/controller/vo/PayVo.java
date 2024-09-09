package cn.edu.xmu.oomall.order.controller.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PayVo {
    Long point;
    Long shopChannel;
    List<Long> coupons;
}
