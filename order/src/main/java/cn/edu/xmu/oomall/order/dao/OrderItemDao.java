package cn.edu.xmu.oomall.order.dao;

import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
import cn.edu.xmu.oomall.order.mapper.generator.OrderItemPoMapper;
import cn.edu.xmu.oomall.order.mapper.generator.po.OrderItemPo;
import cn.edu.xmu.oomall.order.mapper.generator.po.OrderItemPoExample;
import cn.edu.xmu.oomall.order.mapper.generator.po.OrderPoExample;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@Repository
public class OrderItemDao {

    private OrderItemPoMapper orderItemPoMapper;

    @Autowired
    public OrderItemDao(OrderItemPoMapper orderItemPoMapper){
        this.orderItemPoMapper=orderItemPoMapper;
    }

    public OrderItem getBo(OrderItemPo po){
        OrderItem bo = cloneObj(po,OrderItem.class);
        return bo;
    }



    public PageInfo<OrderItem> retrieveByOrderId(Long orderId){
        List<OrderItem> ret =null;
        OrderItemPoExample example = new OrderItemPoExample();
        OrderItemPoExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<OrderItemPo> poList = orderItemPoMapper.selectByExample(example);
        if(poList.size()>0){
            ret=poList.stream().map(this::getBo).collect(Collectors.toList());
        }
        return new PageInfo<>(ret);
    }

    public PageInfo<OrderItem> retrieveByActivityId(Long activityId){
        List<OrderItem> ret =new ArrayList<>();
        OrderItemPoExample example = new OrderItemPoExample();
        OrderItemPoExample.Criteria criteria = example.createCriteria();
        criteria.andActivityIdEqualTo(activityId);
        List<OrderItemPo> poList = orderItemPoMapper.selectByExample(example);
        if(poList.size()>0){
            ret=poList.stream().map(this::getBo).collect(Collectors.toList());
        }
        return new PageInfo<>(ret);
    }
}
