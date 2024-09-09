package cn.edu.xmu.oomall.order.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.order.dao.openfeign.PaymentDao;
import lombok.Data;

@Data
public class OrderPayment extends OOMallObject {
    private Long orderId;

    private Long paymentId;

}
