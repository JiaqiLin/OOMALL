package cn.edu.xmu.oomall.order.service.responsibilityChain;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
import cn.edu.xmu.oomall.order.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.order.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.FreightDto;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.ItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ExpressFeeHandler extends Handler {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private ShopDao shopDao;
    @Override
    public void handle(List<OrderItem> orderItems, Long shopId,Long regionId) {
        List<OrderItem> orderItems2=new ArrayList<>(orderItems);


        Map<Long,List<ItemDto>> packs=new HashMap<>();
        //将使用相同运费模板的物品合到一起
        orderItems.stream().forEach(orderItem -> {

            ItemDto itemDto = ItemDto.builder().orderItemId(orderItem.getId()).productId(orderItem.getProductId()).quantity(orderItem.getQuantity()).weight(Math.toIntExact(orderItem.getWeight())).build();
            Long freightTemplateId = orderItem.getFreightTemplateId();
            List<ItemDto> pack = packs.get(freightTemplateId);
            if(null==pack){
                packs.put(freightTemplateId,new ArrayList<ItemDto>(){
                    {
                        add(itemDto);
                    }
                });
            }else{
                pack.add(itemDto);
            }
        });

        Long fee= Long.valueOf(packs.keySet().stream().map(freightTemplateId-> {
            InternalReturnObject<FreightDto> fdto = shopDao.getFreight(freightTemplateId,regionId,packs.get(freightTemplateId));
            return fdto.getData().getFreight();
        }).reduce((x,y)->x+y).get());
        setFee(fee);
        if(this.next!=null)
            next.handle(orderItems2,shopId,regionId);
    }
}
