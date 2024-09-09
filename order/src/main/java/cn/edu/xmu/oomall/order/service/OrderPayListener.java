package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.order.controller.vo.PayVo;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.dao.OrderPaymentDao;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.dao.openfeign.PaymentDao;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.SimpleShopChannelDto;
import cn.edu.xmu.oomall.order.service.dto.MessageOrderDto;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RocketMQTransactionListener(rocketMQTemplateBeanName="orderPayRocketMQTemplate")
public class OrderPayListener implements RocketMQLocalTransactionListener {
    private OrderService orderService;

    private OrderDao orderDao;

    private PaymentDao paymentDao;
    @Autowired
    public OrderPayListener(OrderService orderService, OrderDao orderDao, PaymentDao paymentDao) {
        this.orderService = orderService;
        this.orderDao = orderDao;
        this.paymentDao = paymentDao;
    }

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        String obj1 = (String)msg.getHeaders().get("user");
        UserDto user = JacksonUtil.toObj(obj1, UserDto.class);
        String obj2 = (String)msg.getHeaders().get("pay");
        PayVo payVo = JacksonUtil.toObj(obj2, PayVo.class);
        String body = new String((byte[]) msg.getPayload(), StandardCharsets.UTF_8);
        MessageOrderDto messageOrderDto = JacksonUtil.toObj(body, MessageOrderDto.class);
        Order order = orderDao.findById(messageOrderDto.getId());
        try{
            Long channelId = null;
            if(payVo != null) {
                order.setPoint(payVo.getPoint());
                channelId = payVo.getShopChannel();
            }
            else{
                SimpleShopChannelDto simpleShopChannelDto = paymentDao.retrieveShopChannels(order.getShopId()).get(0);
                channelId = simpleShopChannelDto.getId();
            }
            order.setStatus(Order.PAID);
            orderDao.saveById(order, user);
            orderService.payOrder(order, channelId, user);
        }catch (Exception e) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        return RocketMQLocalTransactionState.COMMIT;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        String body = new String((byte[]) msg.getPayload(), StandardCharsets.UTF_8);
        MessageOrderDto messageOrderDto = JacksonUtil.toObj(body, MessageOrderDto.class);
        Order order1=orderDao.findById(messageOrderDto.getId());
        if(order1.getStatus()==Order.PAID){
            return RocketMQLocalTransactionState.COMMIT;
        }else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}
