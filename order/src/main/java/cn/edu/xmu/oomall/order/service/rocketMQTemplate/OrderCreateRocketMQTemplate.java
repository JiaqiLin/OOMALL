package cn.edu.xmu.oomall.order.service.rocketMQTemplate;

import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

@ExtRocketMQTemplateConfiguration(group = "order_group_create")
@Component
public class OrderCreateRocketMQTemplate extends RocketMQTemplate {
}
