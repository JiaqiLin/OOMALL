package cn.edu.xmu.oomall.order.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.dao.bo.OrderRefund;
import cn.edu.xmu.oomall.order.mapper.generator.OrderRefundPoMapper;
import cn.edu.xmu.oomall.order.mapper.generator.po.OrderPo;
import cn.edu.xmu.oomall.order.mapper.generator.po.OrderPoExample;
import cn.edu.xmu.oomall.order.mapper.generator.po.OrderRefundPo;
import cn.edu.xmu.oomall.order.mapper.generator.po.OrderRefundPoExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class OrderRefundDao  {
    private static final Logger logger = LoggerFactory.getLogger(OrderRefundDao.class);

    private OrderRefundPoMapper orderRefundPoMapper;

    @Autowired
    public OrderRefundDao(OrderRefundPoMapper orderRefundPoMapper){
        this.orderRefundPoMapper=orderRefundPoMapper;
    }

    public OrderRefund findByOrderId(Long orderId) throws RuntimeException{
        OrderRefund ret=null;
        if(null!=orderId){
            OrderRefundPoExample example=new OrderRefundPoExample();
            OrderRefundPoExample.Criteria criteria=example.createCriteria();
            criteria.andOrderIdEqualTo(orderId);
            List<OrderRefundPo> poList=this.orderRefundPoMapper.selectByExample(example);
            if(poList.size()>0){
                ret=cloneObj(poList.get(0),OrderRefund.class);
            }else {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"订单退款", orderId));
            }
        }
        return ret;
    }

    public void save(OrderRefund obj, UserDto user) throws RuntimeException{
        logger.debug("insertObj: obj = {}", obj);
        OrderRefundPo po = cloneObj(obj, OrderRefundPo.class);
        putUserFields(po, "creator", user);
        putGmtFields(po, "create");
        logger.debug("insertObj: po = {}", po);
        orderRefundPoMapper.insertSelective(po);
    }
}
