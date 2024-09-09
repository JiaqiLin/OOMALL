//package cn.edu.xmu.oomall.order.service;
//
//import cn.edu.xmu.javaee.core.exception.BusinessException;
//import cn.edu.xmu.javaee.core.model.InternalReturnObject;
//import cn.edu.xmu.javaee.core.model.dto.UserDto;
//import cn.edu.xmu.oomall.order.OrderTestApplication;
//import cn.edu.xmu.oomall.order.controller.vo.PayVo;
//import cn.edu.xmu.oomall.order.controller.vo.UpdateOrderVo;
//import cn.edu.xmu.oomall.order.dao.OrderPaymentDao;
//import cn.edu.xmu.oomall.order.dao.bo.Order;
//import cn.edu.xmu.oomall.order.dao.bo.OrderPayment;
//import cn.edu.xmu.oomall.order.dao.openfeign.PaymentDao;
//import cn.edu.xmu.oomall.order.dao.openfeign.dto.FullPayTransDto;
//import cn.edu.xmu.oomall.order.dao.openfeign.dto.RefundTransDto;
//import cn.edu.xmu.oomall.order.dao.openfeign.dto.ShopChannelDto;
//import cn.edu.xmu.oomall.order.dao.openfeign.dto.SimplePayTransDto;
//import cn.edu.xmu.oomall.order.service.dto.OrderDto;
//import cn.edu.xmu.oomall.order.service.dto.OrderStatusDto;
//import cn.edu.xmu.oomall.order.service.rocketMQTemplate.OrderCancelRocketMQTemplate;
//import cn.edu.xmu.oomall.order.service.rocketMQTemplate.OrderPayRocketMQTemplate;
//import org.apache.rocketmq.client.producer.TransactionSendResult;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@SpringBootTest(classes = OrderTestApplication.class)
//@Transactional
//public class OrderService1Test {
//
//    @Autowired
//    private OrderService orderService;
//
//    @MockBean
//    private PaymentDao paymentDao;
//
//    @MockBean
//    private OrderPaymentDao orderPaymentDao;
//
//    @MockBean
//    private OrderCancelRocketMQTemplate orderCancelRocketMQTemplate;
//
//    @MockBean
//    private OrderPayRocketMQTemplate orderPayRocketMQTemplate;
//
//    /**
//     * 顾客修改订单，成功
//     */
//    @Test
//    public void updateOrderByCustomerTest1(){
//        UpdateOrderVo orderVo=new UpdateOrderVo();
//        orderVo.setConsignee("张三");
//        orderVo.setAddress("厦门大学");
//        orderVo.setMobile("12345678911");
//        orderVo.setRegionId(1L);
//        UserDto user = new UserDto();
//        user.setId(Long.valueOf(2));
//        user.setName("test1");
//        user.setUserLevel(1);
//        orderService.updateOrderByCustomer(2L,orderVo,user);
//    }
//
//    /**
//     * 顾客修改订单，订单状态不可修改
//     */
//    @Test
//    public void updateOrderByCustomerTest2(){
//        UpdateOrderVo orderVo=new UpdateOrderVo();
//        orderVo.setConsignee("张三");
//        orderVo.setAddress("厦门大学");
//        orderVo.setMobile("12345678911");
//        orderVo.setRegionId(1L);
//        UserDto user = new UserDto();
//        user.setId(Long.valueOf(1));
//        user.setName("test1");
//        user.setUserLevel(1);
//        assertThrows(BusinessException.class,()->orderService.updateOrderByCustomer(1L,orderVo,user)) ;
//    }
//    /**
//     * 顾客修改订单，订单不为该顾客的
//     */
//    @Test
//    public void updateOrderByCustomerTest3(){
//        UpdateOrderVo orderVo=new UpdateOrderVo();
//        orderVo.setConsignee("张三");
//        orderVo.setAddress("厦门大学");
//        orderVo.setMobile("12345678911");
//        orderVo.setRegionId(1L);
//        UserDto user = new UserDto();
//        user.setId(Long.valueOf(2));
//        user.setName("test1");
//        user.setUserLevel(1);
//        assertThrows(BusinessException.class,()->orderService.updateOrderByCustomer(1L,orderVo,user)) ;
//    }
//
//    /**
//     * 顾客取消订单测试，成功
//     */
//    @Test
//    public void cancelOrderByCustomerTest1(){
//        //调用支付模块获取支付单
//        FullPayTransDto fullPayTransDto= new FullPayTransDto();
//        fullPayTransDto.setId(1L);
//        fullPayTransDto.setAmount(100L);
//        fullPayTransDto.setDivAmount(20L);
//        Mockito.when(paymentDao.getPayment(Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(fullPayTransDto));
//        //调用支付模块退款
//        RefundTransDto refundTransDto= new RefundTransDto();
//        refundTransDto.setId(1L);
//        Mockito.when(paymentDao.createRefund(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(refundTransDto));
//        OrderPayment orderPayment= new OrderPayment();
//        orderPayment.setId(1L);
//        orderPayment.setPaymentId(1L);
//        List<OrderPayment> orderPaymentList=new ArrayList<>();
//        orderPaymentList.add(orderPayment);
//        Mockito.when(orderPaymentDao.retrieveByOrderId((Mockito.any()))).thenReturn(orderPaymentList);
//        Mockito.when(orderCancelRocketMQTemplate.sendMessageInTransaction(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new TransactionSendResult());
//        UserDto user = new UserDto();
//        user.setId(Long.valueOf(2));
//        user.setName("test1");
//        user.setUserLevel(1);
//        orderService.cancelOrderByCustomer(2L,user);
//    }
//
//    /**
//     * 顾客取消订单，订单非顾客本人的
//     */
//    @Test
//    public void cancelOrderByCustomerTest2(){
//        UserDto user = new UserDto();
//        user.setId(Long.valueOf(2));
//        user.setName("test1");
//        user.setUserLevel(1);
//        assertThrows(BusinessException.class,()->orderService.cancelOrderByCustomer(1L,user));
//    }
//
//    /**
//     * 顾客取消订单，订单状态不可取消
//     */
//    @Test
//    public void cancelOrderByCustomerTest3(){
//        UserDto user = new UserDto();
//        user.setId(Long.valueOf(1));
//        user.setName("test1");
//        user.setUserLevel(1);
//        assertThrows(BusinessException.class,()->orderService.cancelOrderByCustomer(1L,user));
//    }
//
//    @Test
//    public void retrieveOrderTest1(){
//        UserDto user = new UserDto();
//        user.setId(Long.valueOf("1"));
//        user.setName("test1");
//        OrderDto orderDto = orderService.retrieveOrder(1L, 1L, user);
//        assertEquals("长通型牛奶杯", orderDto.getOrderItems().get(0).getName());
//    }
//
//    /**
//     * 顾客支付订单，成功
//     */
//    @Test
//    public void payOrderByCustomerTest1(){
//        //调用支付模块获取支付渠道
//        ShopChannelDto shopChannelDto = new ShopChannelDto();
//        shopChannelDto.setId(1L);
//        Mockito.when(paymentDao.getShopChannel(Mockito.any(), Mockito.any())).thenReturn(shopChannelDto);
//        //调用支付模块完成支付，获取支付单
//        SimplePayTransDto simplePayTransDto = new SimplePayTransDto();
//        simplePayTransDto.setId(1L);
//        simplePayTransDto.setPrepayId("123456");
//        Mockito.when(paymentDao.createPayment(Mockito.any())).thenReturn(new InternalReturnObject<>(simplePayTransDto));
//        Mockito.when(orderPayRocketMQTemplate.sendMessageInTransaction(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new TransactionSendResult());
//        PayVo payVo = new PayVo();
//        payVo.setPoint(200L);
//        payVo.setShopChannel(1L);
//        UserDto user = new UserDto();
//        user.setId(2L);
//        user.setName("test1");
//        orderService.payOrderByCustomer(200L, payVo, user);
//    }
//
//    /**
//     * 顾客支付订单，订单状态不可支付
//     */
//    @Test
//    public void payOrderByCustomerTest2(){
//        UserDto user = new UserDto();
//        user.setId(1L);
//        user.setName("test2");
//        PayVo payVo = new PayVo();
//        assertThrows(BusinessException.class, () -> orderService.payOrderByCustomer(1L, payVo, user));
//    }
//
//    /**
//     * 顾客支付订单，该订单顾客非该顾客
//     */
//    @Test
//    public void payOrderByCustomerTest3(){
//        UserDto user = new UserDto();
//        user.setId(1L);
//        user.setName("test2");
//        PayVo payVo = new PayVo();
//        assertThrows(BusinessException.class, () -> orderService.payOrderByCustomer(200L, payVo, user));
//    }
//
//
//    @Test
//    public void retrieveAllOrderStatusTest(){
//        List<OrderStatusDto> list = (List<OrderStatusDto>) orderService.retrieveAllOrderStatus().getData();
//        assertEquals(25212,list.size());
//        assertEquals(300,list.get(0).getCode());
//        assertEquals("已完成",list.get(0).getName());
//    }
//
//
//    @Test
//    public void refundOrderTest(){
//        //调用支付模块获取支付单
//        FullPayTransDto fullPayTransDto= new FullPayTransDto();
//        fullPayTransDto.setId(1L);
//        fullPayTransDto.setAmount(100L);
//        fullPayTransDto.setDivAmount(20L);
//        Mockito.when(paymentDao.getPayment(Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(fullPayTransDto));
//        //调用支付模块退款
//        RefundTransDto refundTransDto= new RefundTransDto();
//        refundTransDto.setId(1L);
//        Mockito.when(paymentDao.createRefund(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(refundTransDto));
//        OrderPayment orderPayment=new OrderPayment();
//        orderPayment.setOrderId(6L);
//        orderPayment.setPaymentId(1L);
//        Order order=new Order();
//        order.setShopId(1L);
//        order.setId(1L);
//        UserDto user = new UserDto();
//        user.setId(Long.valueOf(2));
//        user.setName("test1");
//        user.setUserLevel(1);
//        orderService.refundOrder(orderPayment,order,user);
//    }
//
//    @Test
//    public void payOrderTest(){
//        ShopChannelDto shopChannelDto = new ShopChannelDto();
//        shopChannelDto.setId(1L);
//        Mockito.when(paymentDao.getShopChannel(Mockito.any(), Mockito.any())).thenReturn(shopChannelDto);
//        //调用支付模块完成支付，获取支付单
//        SimplePayTransDto simplePayTransDto = new SimplePayTransDto();
//        simplePayTransDto.setId(1L);
//        simplePayTransDto.setPrepayId("123456");
//        Mockito.when(paymentDao.createPayment(Mockito.any())).thenReturn(new InternalReturnObject<>(simplePayTransDto));
//        UserDto user = new UserDto();
//        user.setId(2L);
//        user.setName("test1");
//        Order order = new Order();
//        order.setId(200L);
//        order.setShopId(1L);
//        order.setCustomerId(2L);
//        order.setOriginPrice(500L);
//        order.setDiscountPrice(0L);
//        order.setPoint(100L);
//        order.setExpressFee(67L);
//        orderService.payOrder(order, 1L, user);
//    }
//}
