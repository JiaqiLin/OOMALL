package cn.edu.xmu.oomall.order.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import lombok.Data;

@Data
public class OrderRefund extends OOMallObject {
    private Long orderId;

    private Long refundId;

    private Integer point;
}
