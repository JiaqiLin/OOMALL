package cn.edu.xmu.oomall.order.service.responsibilityChain;

import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Repository
public class DiscountPriceHandler extends Handler{

    @Override
    public void handle(List<OrderItem> orderItems, Long shopId,Long regionId) {
        List<OrderItem> orderItems1=new ArrayList<>(orderItems);
        setFee(orderItems.stream().map(orderItem -> orderItem.getDiscountPrice()).reduce((x,y)->{return x+y;}).get());
        if(this.next!=null)
            next.handle(orderItems1,shopId,regionId);
    }
}
