package cn.edu.xmu.oomall.order.dao.openfeign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayDto {
    private LocalDateTime timeExpire;//结束支付时间
    private LocalDateTime timeBegin;//开始支付时间
    private String spOpenid;//支付用户标识
    private Long amount;//付款金额
    private Long divAmount;//付款分账金额
    private Long shopChannelId;//商铺支付渠道id
}
