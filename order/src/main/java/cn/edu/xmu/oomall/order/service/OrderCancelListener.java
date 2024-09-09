//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.order.controller.vo.PayVo;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.dao.OrderPaymentDao;
import cn.edu.xmu.oomall.order.dao.OrderRefundDao;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
import cn.edu.xmu.oomall.order.dao.bo.OrderPayment;
import cn.edu.xmu.oomall.order.dao.bo.OrderRefund;
import cn.edu.xmu.oomall.order.dao.openfeign.PaymentDao;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.FullPayTransDto;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.RefundDto;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.RefundTransDto;
import cn.edu.xmu.oomall.order.service.dto.ConsigneeDto;
import cn.edu.xmu.oomall.order.service.dto.MessageOrderDto;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RocketMQTransactionListener(rocketMQTemplateBeanName="orderCancelRocketMQTemplate")
public class OrderCancelListener implements RocketMQLocalTransactionListener {


    private  OrderService orderService;

    private OrderDao orderDao;

    private OrderRefundDao orderRefundDao;

    private OrderPaymentDao orderPaymentDao;

    private PaymentDao paymentDao;

    @Autowired
    public OrderCancelListener(OrderService orderService,OrderDao orderDao,OrderPaymentDao orderPaymentDao,OrderRefundDao orderRefundDao,PaymentDao paymentDao) {
        this.orderService = orderService;
        this.orderDao=orderDao;
        this.orderRefundDao=orderRefundDao;
        this.orderPaymentDao=orderPaymentDao;
        this.paymentDao=paymentDao;
    }

    /**
     * 事务消息发送成功回调
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        String obj=(String)msg.getHeaders().get("user");
        UserDto user=JacksonUtil.toObj(obj, UserDto.class);
        String body = new String((byte[]) msg.getPayload(), StandardCharsets.UTF_8);
        MessageOrderDto messageOrderDto = JacksonUtil.toObj(body, MessageOrderDto.class);
        Order order=orderDao.findById(messageOrderDto.getId());
        try{
            //判断订单类型
            if(order.getStatus()==Order.BALANCE){
                //将数据库中订单修改为已取消
                order.setStatus(Order.CANCEL);
                orderDao.saveById(order,user);
            } else {
                //将数据库中订单修改为待退款
                order.setStatus(Order.REFUND);
                orderDao.saveById(order,user);
                List<OrderPayment> orderPaymentList = orderPaymentDao.retrieveByOrderId(order.getId());
                OrderPayment orderPayment=new OrderPayment();
                //有两笔支付单是预售已支付订单，只退时间后的那一笔
                if(orderPaymentList.size()>1){
                    orderPayment= Duration.between(orderPaymentList.get(0).getGmtCreate(),orderPaymentList.get(1).getGmtCreate()).toMillis()>0?orderPaymentList.get(1):orderPaymentList.get(0);
                }else {
                    orderPayment=orderPaymentList.get(0);
                }
                orderService.refundOrder(orderPayment,order,user);
            }
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
        if(order1.getStatus()==Order.CANCEL){
            return RocketMQLocalTransactionState.COMMIT;
        }else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}

