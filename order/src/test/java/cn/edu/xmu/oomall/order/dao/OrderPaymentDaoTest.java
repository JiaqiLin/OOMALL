//package cn.edu.xmu.oomall.order.dao;
//
//import cn.edu.xmu.javaee.core.model.dto.UserDto;
//import cn.edu.xmu.oomall.order.OrderTestApplication;
//import cn.edu.xmu.oomall.order.dao.bo.OrderPayment;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//
//@SpringBootTest(classes = OrderTestApplication.class)
//@Transactional
//public class OrderPaymentDaoTest {
//    @Autowired
//    private OrderPaymentDao orderPaymentDao;
//    @Test
//    public void saveTest1(){
//        UserDto userDto = new UserDto();
//        userDto.setId(1L);
//        userDto.setName("test1");
//        OrderPayment orderPayment = new OrderPayment();
//        orderPayment.setId(1L);
//        orderPayment.setOrderId(1L);
//        orderPayment.setGmtCreate(LocalDateTime.now());
//        orderPaymentDao.save(orderPayment, userDto);
//        OrderPayment obj = orderPaymentDao.findByOrderId(1L);
//        System.out.println(obj);
//    }
//}
