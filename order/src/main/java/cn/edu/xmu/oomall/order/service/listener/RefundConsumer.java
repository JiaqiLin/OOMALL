package cn.edu.xmu.oomall.order.service.listener;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.dao.OrderItemDao;
import cn.edu.xmu.oomall.order.dao.OrderPaymentDao;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
import cn.edu.xmu.oomall.order.dao.bo.OrderPayment;
import cn.edu.xmu.oomall.order.service.OrderService;
import cn.edu.xmu.oomall.order.service.dto.ActivityDto;
import cn.edu.xmu.oomall.order.service.dto.MessageActivityDto;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RocketMQMessageListener(topic = "Refund", consumerGroup = "order-refund", consumeThreadMax = 1)
public class RefundConsumer implements RocketMQListener<Message> {

    private static final Logger logger = LoggerFactory.getLogger(RefundConsumer.class);

    private OrderItemDao orderItemDao;

    private OrderDao orderDao;

    private OrderPaymentDao orderPaymentDao;

    private OrderService orderService;

    @Autowired
    public RefundConsumer(OrderItemDao orderItemDao,OrderDao orderDao,OrderPaymentDao orderPaymentDao,OrderService orderService){
        this.orderItemDao=orderItemDao;
        this.orderDao=orderDao;
        this.orderService=orderService;
        this.orderPaymentDao=orderPaymentDao;
    }
    @Override
    public void onMessage(Message message) {
        try {
            String content = new String(message.getBody(), "UTF-8");
            MessageActivityDto messageActivityDto = JacksonUtil.toObj(content, MessageActivityDto.class);
            if (null == messageActivityDto){
                logger.error("RefundConsumer: wrong format.... content = {}",content);
            }else{
                UserDto user=new UserDto();
                user.setId(messageActivityDto.getUserDto().getId());
                user.setName(messageActivityDto.getUserDto().getName());
                //根据活动id找到order_item,根据orderitem找到order,对每个order进行全额退款
                orderItemDao.retrieveByActivityId(messageActivityDto.getId()).getList().stream()
                        .map(orderItem -> orderDao.findById(orderItem.getOrderId()))
                        .forEach(order -> orderPaymentDao.retrieveByOrderId(order.getId())
                                          .forEach(orderPayment -> orderService.refundOrder(orderPayment,order,user))
                            );
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("RefundConsumer: wrong encoding.... msg = {}",message);
        }
    }
}
