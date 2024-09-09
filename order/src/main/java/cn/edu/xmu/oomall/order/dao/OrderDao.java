//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
import cn.edu.xmu.oomall.order.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.OnsaleDto;
import cn.edu.xmu.oomall.order.mapper.generator.OrderItemPoMapper;
import cn.edu.xmu.oomall.order.mapper.generator.OrderPoMapper;
import cn.edu.xmu.oomall.order.mapper.generator.po.OrderItemPo;
import cn.edu.xmu.oomall.order.mapper.generator.po.OrderItemPoExample;
import cn.edu.xmu.oomall.order.mapper.generator.po.OrderPo;
import cn.edu.xmu.oomall.order.mapper.generator.po.OrderPoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;
import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class OrderDao {

    private static  final Logger logger = LoggerFactory.getLogger(Order.class);

    private OrderPoMapper orderPoMapper;

    private OrderItemPoMapper orderItemPoMapper;

    private GoodsDao goodsDao;

    private OrderItemDao orderItemDao;


    @Autowired
    public OrderDao(OrderPoMapper orderPoMapper, OrderItemPoMapper orderItemPoMapper,GoodsDao goodsDao,OrderItemDao orderItemDao) {
        this.orderPoMapper = orderPoMapper;
        this.orderItemPoMapper = orderItemPoMapper;
        this.goodsDao=goodsDao;
        this.orderItemDao=orderItemDao;
    }

    public Order getBo(OrderPo po){
        Order bo = cloneObj(po, Order.class);
        bo.setOrderItemDao(orderItemDao);
        return bo;
    }


    public void createOrder(Order order){
        OrderPo orderPo = OrderPo.builder().status(Order.NEW).creatorId(order.getCreatorId()).creatorName(order.getCreatorName()).orderSn(order.getOrderSn())
                .build();
        orderPoMapper.insertSelective(orderPo);
        OrderPoExample example = new OrderPoExample();
        OrderPoExample.Criteria criteria = example.createCriteria();
        criteria.andOrderSnEqualTo(order.getOrderSn());

        orderPo = orderPoMapper.selectByExample(example).get(0);
        OrderPo finalOrderPo = orderPo;

        order.getOrderItems().stream().forEach(orderItem -> {

            OrderItemPo orderItemPo = OrderItemPo.builder().creatorId(orderItem.getCreatorId())
                    .onsaleId(orderItem.getOnsaleId()).quantity(orderItem.getQuantity())
                    .price(orderItem.getPrice()).name(orderItem.getName()).couponId(orderItem.getCouponId())
                    .activityId(order.getActivityId())
                    .quantity(orderItem.getQuantity()).orderId(finalOrderPo.getId()).build();
            orderItemPoMapper.insertSelective(orderItemPo);
        });

        orderPo.setDiscountPrice(order.getDiscountPrice());
        orderPo.setExpressFee(order.getExpressFee());
        orderPo.setDiscountPrice(order.getDiscountPrice());
        this.orderPoMapper.updateByPrimaryKeySelective(orderPo);

    }

    public Order findById(Long id)throws RuntimeException{
        Order ret = null;
        if(null != id){
            OrderPo po = orderPoMapper.selectByPrimaryKey(id);
            ret = this.getBo(po);
        }
        List<OrderItem> list = new ArrayList<>();
        OrderItemPoExample orderItemPoExample = new OrderItemPoExample();
        OrderItemPoExample.Criteria criteria = orderItemPoExample.createCriteria();
        criteria.andOrderIdEqualTo(id);
        List<OrderItemPo> poList = this.orderItemPoMapper.selectByExample(orderItemPoExample);
        if(poList.size()>0){
            list = poList.stream().map(po-> {
                OrderItem orderItem = cloneObj(po, OrderItem.class);
                return orderItem;
            }).collect(Collectors.toList());
        }
        ret.setOrderItems(list);
        return ret;
    }

    public ReturnObject saveById(Order order, UserDto user)throws RuntimeException{
        if(null!=order&&null!=order.getId()){
            OrderPo po=cloneObj(order,OrderPo.class);
            if (null != user) {
                putUserFields(po, "modifier", user);
                putGmtFields(po, "modified");
            }
            int ret = orderPoMapper.updateByPrimaryKeySelective(po);
            if(0 == ret){
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"订单", po.getId()));
            }
        }
        return new ReturnObject(ReturnNo.OK);
    }


    public Order findByShopIdAndOrderId(Long shopId,Long orderId) throws RuntimeException{
        Order ret=null;
        if(null!=shopId&&null!=orderId){
            OrderPoExample example=new OrderPoExample();
            OrderPoExample.Criteria criteria=example.createCriteria();
            criteria.andShopIdEqualTo(shopId).andIdEqualTo(orderId);
            List<OrderPo> poList=this.orderPoMapper.selectByExample(example);
            if(poList.size()>0){
                ret=cloneObj(poList.get(0),Order.class);
            }else {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
        }
        return ret;
    }

    public List<Order> retrieveAllOrder(){
        return orderPoMapper.selectByExample(null).stream().map(this::getBo).collect(Collectors.toList());
    }

    public List<Order> retrieveByShopId(Long shopId,Long customerId, String orderSn, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize){
        List<Order> ret=new ArrayList<>();
        OrderPoExample example=new OrderPoExample();
        OrderPoExample.Criteria criteria=example.createCriteria();
        criteria.andShopIdEqualTo(shopId);
        if(null!=customerId){
            criteria.andCustomerIdEqualTo(customerId);
        }
        if(null!=orderSn){
            criteria.andOrderSnEqualTo(orderSn);
        }
        if(null!=beginTime){
            criteria.andGmtCreateGreaterThanOrEqualTo(beginTime);
        }
        if(null!=endTime){
            criteria.andGmtCreateLessThanOrEqualTo(endTime);
        }
        PageHelper.startPage(page,pageSize,false);
        List<OrderPo> poList=orderPoMapper.selectByExample(example);
        if(poList.size()>0){
            ret=poList.stream().map(po->this.getBo(po)).collect(Collectors.toList());
        }else{
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"订单概要",shopId));
        }
        return ret;
    }

    /***
     * @description: 根据订单号和订单状态查询当前买家的订单简要信息列表
     * @return: com.github.pagehelper.PageInfo<cn.edu.xmu.oomall.order.dao.bo.Order>
     * @author
     * @date: 21:54 2022/12/26
     */
    public PageInfo<Order> retrieveByOrderSnAndStatus(String orderSn, Integer status, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize){
        List<Order> ret=null;
        OrderPoExample example=new OrderPoExample();
        OrderPoExample.Criteria criteria=example.createCriteria();

        if(null!=status){
            criteria.andStatusEqualTo(status);
        }
        if(null!=orderSn){
            criteria.andOrderSnEqualTo(orderSn);
        }
        if(null!=beginTime){
            criteria.andGmtCreateGreaterThanOrEqualTo(beginTime);
        }
        if(null!=endTime){
            criteria.andGmtCreateLessThanOrEqualTo(endTime);
        }
        PageHelper.startPage(page,pageSize,false);
        List<OrderPo> poList=orderPoMapper.selectByExample(example);
        if(poList.size()>0){
            ret=poList.stream().map(this::getBo).collect(Collectors.toList());
        }else{
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"订单概要",orderSn));
        }
        return new PageInfo<>(ret);
    }


    public ReturnObject delById(Order order)throws RuntimeException{
        if(null!=order.getId()){
            orderPoMapper.deleteByPrimaryKey(order.getId());
            return new ReturnObject(ReturnNo.OK);
        }
        throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"订单不存在", order.getId()));
    }




}
