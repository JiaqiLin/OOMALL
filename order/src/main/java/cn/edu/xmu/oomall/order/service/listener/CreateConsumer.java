package cn.edu.xmu.oomall.order.service.listener;

import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(topic = "Create", consumerGroup = "order-create", consumeThreadMax = 1)
public class CreateConsumer implements RocketMQListener<Message> {
    @Override
    public void onMessage(Message message) {

    }
}
