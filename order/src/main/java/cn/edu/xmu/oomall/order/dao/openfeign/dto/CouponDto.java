package cn.edu.xmu.oomall.order.dao.openfeign.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Builder
public class CouponDto {//只节选了自己需要的
    private ActivityDto activityDto;
    private int state;//优惠券状态 0未用 1已使用
    private LocalDateTime beginTime;//活动开始时间
    private LocalDateTime endTime;//活动结束时间
}
