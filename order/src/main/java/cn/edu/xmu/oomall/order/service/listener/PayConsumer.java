package cn.edu.xmu.oomall.order.service.listener;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RocketMQMessageListener(topic = "Pay", consumerGroup = "order-pay", consumeThreadMax = 1)
public class PayConsumer implements RocketMQListener<Message> {
    private static final Logger logger = LoggerFactory.getLogger(PayConsumer.class);

    @Override
    public void onMessage(Message message){

    }
}
