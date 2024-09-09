package cn.edu.xmu.oomall.order.service.responsibilityChain;

import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public abstract class Handler {
    protected Handler next;

    @Getter
    @Setter
    protected Long fee;



    public void setNext(Handler next) {
        this.next = next;
    }

    public abstract void handle(List<OrderItem> orderItems,Long shopId,Long regionId);
}
