package cn.edu.xmu.oomall.order.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.dao.bo.OrderPayment;
import cn.edu.xmu.oomall.order.dao.bo.OrderRefund;
import cn.edu.xmu.oomall.order.mapper.generator.OrderPaymentPoMapper;
import cn.edu.xmu.oomall.order.mapper.generator.OrderRefundPoMapper;
import cn.edu.xmu.oomall.order.mapper.generator.po.OrderPaymentPo;
import cn.edu.xmu.oomall.order.mapper.generator.po.OrderPaymentPoExample;
import cn.edu.xmu.oomall.order.mapper.generator.po.OrderRefundPo;
import cn.edu.xmu.oomall.order.mapper.generator.po.OrderRefundPoExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class OrderPaymentDao {
    private static final Logger logger = LoggerFactory.getLogger(OrderPaymentDao.class);

    private OrderPaymentPoMapper orderPaymentPoMapper;

    public OrderPayment getBo(OrderPaymentPo po){
        OrderPayment bo = cloneObj(po, OrderPayment.class);
        return bo;
    }

    @Autowired
    public OrderPaymentDao(OrderPaymentPoMapper orderPaymentPoMapper){this.orderPaymentPoMapper=orderPaymentPoMapper;}

    public OrderPayment findByOrderId(Long orderId) throws RuntimeException{
        OrderPayment ret=null;
        if(null!=orderId){
            OrderPaymentPoExample example=new OrderPaymentPoExample();
            OrderPaymentPoExample.Criteria criteria=example.createCriteria();
            criteria.andOrderIdEqualTo(orderId);
            List<OrderPaymentPo> poList=this.orderPaymentPoMapper.selectByExample(example);
            if(poList.size()>0){
                ret=cloneObj(poList.get(0),OrderPayment.class);
            }else {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"订单支付", orderId));
            }
        }
        return ret;
    }

    public List<OrderPayment> retrieveByOrderId(Long orderId) throws RuntimeException{
        List<OrderPayment> ret=new ArrayList<>();
        if(null!=orderId){
            OrderPaymentPoExample example=new OrderPaymentPoExample();
            OrderPaymentPoExample.Criteria criteria=example.createCriteria();
            criteria.andOrderIdEqualTo(orderId);
            List<OrderPaymentPo> poList=this.orderPaymentPoMapper.selectByExample(example);
            if(poList.size()>0){
                ret=poList.stream().map(this::getBo).collect(Collectors.toList()) ;
            }else {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"订单支付", orderId));
            }
        }
        return ret;
    }

    public void save(OrderPayment obj, UserDto user) throws RuntimeException{
        logger.debug("insertObj: obj = {}", obj);
        OrderPaymentPo po = cloneObj(obj, OrderPaymentPo.class);
        putUserFields(po, "creator", user);
        putGmtFields(po, "create");
        logger.debug("insertObj: po = {}", po);
        orderPaymentPoMapper.insertSelective(po);
    }
}
