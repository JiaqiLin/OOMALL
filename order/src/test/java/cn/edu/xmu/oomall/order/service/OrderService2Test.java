//package cn.edu.xmu.oomall.order.service;
//
//
//import cn.edu.xmu.javaee.core.exception.BusinessException;
//import cn.edu.xmu.javaee.core.model.ReturnNo;
//import cn.edu.xmu.javaee.core.model.ReturnObject;
//import cn.edu.xmu.javaee.core.model.dto.PageDto;
//import cn.edu.xmu.javaee.core.model.dto.UserDto;
//import cn.edu.xmu.oomall.order.OrderTestApplication;
//import cn.edu.xmu.oomall.order.dao.OrderDao;
//import cn.edu.xmu.oomall.order.service.dto.OrderSummaryDto;
//import cn.edu.xmu.oomall.order.service.rocketMQTemplate.OrderCancelRocketMQTemplate;
//import cn.edu.xmu.oomall.order.service.rocketMQTemplate.OrderCreateRocketMQTemplate;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@SpringBootTest(classes = OrderTestApplication.class)
//@Transactional
//public class OrderService2Test {
//
//    @Autowired
//    private OrderService orderService;
//
//    private OrderDao orderDao;
//
//    @Test
//    public void updateOrderMessageTest1(){
//        UserDto userDto=null;
//        assertThrows(BusinessException.class,()->orderService.updateOrderMessage(1L,1L,null,userDto));
//    }
//
//    @Test
//    public void updateOrderMessageTest2(){
//        UserDto user=new UserDto();
//        user.setId(Long.valueOf("1"));
//        user.setName("test1");
//        String message="test message";
//        orderService.updateOrderMessage(1L,1L,message,user);
//    }
//
//    @Test
//    public void retrieveOrdersTest1(){
//        UserDto user=null;
//        assertThrows(BusinessException.class,()->orderService.retrieveOrders(1L,null,null,null,null,1,10,user));
//    }
//
//    @Test
//    public void retrieveOrdersTest2(){
//        UserDto user=new UserDto();
//        user.setId(Long.valueOf("1"));
//        user.setName("test1");
//
//        PageDto<OrderSummaryDto> ret=orderService.retrieveOrders(1L,1L,null,null,null,1,10,user);
//        assertEquals(1L,ret.getList().get(0).getId());
//        System.out.println(ret.getList().get(0));
//    }
//
//    @Test
//    public void retrieveCustomerOrdersTest1(){
//        PageDto<OrderSummaryDto> ret = orderService.retrieveCustomerOrders(300, null, null, null, 1, 3, new UserDto(1L, "xxr", 1L, 1));
//        assertEquals(1L,ret.getList().get(0).getId());
//        assertEquals(300,ret.getList().get(0).getStatus());
//    }
//    @Test
//    public void delOrderTest01() {
//        UserDto user = new UserDto();
//        user.setName("test1");
//        user.setUserLevel(1);
//        ReturnObject returnObject = orderService.delOrders(user, 1L, 1L);
//        assertEquals(ReturnNo.OK.getErrNo(),returnObject.getErrno());
//    }
//
//    @Test
//    public void updateOrderStatusTest1(){
//
//        UserDto user = new UserDto();
//        user.setId(Long.valueOf(1));
//        user.setName("test1");
//        // OrderDto ret = orderService.updateOrderStatus(user,1L,1L);
//        orderService.updateOrderStatus(user,1L,1L);
//    }
//
//    @Test
//    public void updateOrderStatusTest2(){
//        UserDto userDto=null;
//        assertThrows(BusinessException.class,()->orderService.updateOrderStatus(userDto,1L,1L));
//    }
//}
