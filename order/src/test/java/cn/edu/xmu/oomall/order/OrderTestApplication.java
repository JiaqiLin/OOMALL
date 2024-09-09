package cn.edu.xmu.oomall.order;

import cn.edu.xmu.javaee.core.CoreApplication;
import cn.edu.xmu.oomall.order.service.OrderCancelListener;
import cn.edu.xmu.oomall.order.service.OrderListener;
import cn.edu.xmu.oomall.order.service.rocketMQTemplate.OrderCancelRocketMQTemplate;
import cn.edu.xmu.oomall.order.service.rocketMQTemplate.OrderCreateRocketMQTemplate;
import cn.edu.xmu.oomall.order.service.rocketMQTemplate.OrderPayRocketMQTemplate;
import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@ComponentScan(basePackages = {"cn.edu.xmu.javaee.core",
        "cn.edu.xmu.oomall.order"},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {RocketMQMessageListener.class,
                        RocketMQTransactionListener.class,SpringBootApplication.class }),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OrderCancelRocketMQTemplate.class, OrderCreateRocketMQTemplate.class,RocketMQTemplate.class, OrderPayRocketMQTemplate.class}),
        })
@SpringBootConfiguration
@EnableAutoConfiguration
@MapperScan("cn.edu.xmu.oomall.order.mapper.generator")
@EnableFeignClients
public class OrderTestApplication {

        public static void main(String[] args) {
                SpringApplication.run(OrderApplication.class, args);

        }
}
