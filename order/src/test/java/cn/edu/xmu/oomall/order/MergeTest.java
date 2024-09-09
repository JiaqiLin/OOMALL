package cn.edu.xmu.oomall.order;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.order.controller.vo.PayVo;
import cn.edu.xmu.oomall.order.controller.vo.UpdateOrderVo;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.dao.OrderItemDao;
import cn.edu.xmu.oomall.order.dao.OrderPaymentDao;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
import cn.edu.xmu.oomall.order.dao.bo.OrderPayment;
import cn.edu.xmu.oomall.order.dao.openfeign.CustomerDao;
import cn.edu.xmu.oomall.order.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.order.dao.openfeign.PaymentDao;
import cn.edu.xmu.oomall.order.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.*;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.ActivityDto;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.CouponDto;
import cn.edu.xmu.oomall.order.service.OrderService;
import cn.edu.xmu.oomall.order.service.dto.*;
import cn.edu.xmu.oomall.order.service.rocketMQTemplate.OrderCancelRocketMQTemplate;
import cn.edu.xmu.oomall.order.service.rocketMQTemplate.OrderCreateRocketMQTemplate;
import cn.edu.xmu.oomall.order.service.rocketMQTemplate.OrderPayRocketMQTemplate;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.edu.xmu.javaee.core.model.Constants.DATE_TIME_FORMATTER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = OrderTestApplication.class)
@Transactional
@AutoConfigureMockMvc
public class MergeTest {
    @Autowired
    private MockMvc mockMvc;


    private static String adminToken;

    private static final String UPDATEORDER = "/orders/{id}";

    @MockBean
    //@Qualifier("orderCancelRocketMQTemplate")
    private OrderCancelRocketMQTemplate orderCancelRocketMQTemplate;

    @MockBean
    //@Qualifier("rocketMQTemplate")
    private OrderCreateRocketMQTemplate orderCreateRocketMQTemplate;

    @MockBean
    private OrderPayRocketMQTemplate orderPayRocketMQTemplate;

    @MockBean
    private RedisUtil redisUtil;

    @Qualifier("rocketMQTemplate")
    private RocketMQTemplate rocketMQTemplate;

    @MockBean
    private PaymentDao paymentDao;

    @MockBean
    private OrderPaymentDao orderPaymentDao;

    @MockBean
    private GoodsDao goodsDao;

    @MockBean
    private ShopDao shopDao;

    @MockBean
    private CustomerDao customerDao;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    /**
     * 用户1没有未完成的订单，无法测试成功路径，所以这里id改成2了，有需要改回1的同学可能得重新建一个ControllerTest
     */
    @BeforeAll
    public static void setup(){
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(2L, "13088admin", 0L, 1, 3600);
    }
    @Test
    public void createOrderTest() throws Exception {
        String body = "{\"items\": [{\"couponId\":1,\"onsaleId\": 1,\"quantity\": 2, \"actId\": 1}],\"consignee\": \"amz\",\"mobile\": \"13017382938\",\"regionId\": 1,\"address\": \"郊区\",\"message\": \"尽快发货\"}";
        Mockito.when(orderCreateRocketMQTemplate.sendMessageInTransaction(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new TransactionSendResult());

        IdNameDto product = new IdNameDto(1L,"牛奶");
        IdNameTypeDto shop = new IdNameTypeDto(1L,"金典官方旗舰店", (byte) 1);
        LocalDateTime saleBegin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
        LocalDateTime saleEnd = LocalDateTime.parse("2024-01-01T12:12:12", DATE_TIME_FORMATTER);
        List<IdNameTypeDto> actList = new ArrayList<>();
        actList.add(new IdNameTypeDto(1L,"优惠活动",(byte)1));
        OnsaleDto onsaleDto = new OnsaleDto(1L,shop,product, 5000L,saleBegin,saleEnd,100,2, (byte) 0,actList);
        Mockito.when(goodsDao.getOnsaleById(Mockito.anyLong(),Mockito.anyLong())).thenReturn(new InternalReturnObject<>(onsaleDto));

        IdNameDto template = new IdNameDto(1L,"一般运费模板");
        FullProductDto fullProductDto = new FullProductDto(1L,"牛奶",5L,250L,template);
        Mockito.when(goodsDao.getProductById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(fullProductDto));

        LocalDateTime actBegin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
        LocalDateTime actEnd = LocalDateTime.parse("2024-01-01T12:12:12", DATE_TIME_FORMATTER);
        ActivityDto activityDto = new ActivityDto(1L,"优惠活动",(byte)0);
        CouponDto couponDto = new CouponDto(activityDto,0,actBegin,actEnd);
        Mockito.when(customerDao.getCouponsById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(couponDto));

        FreightDto freightDto = new FreightDto(5,null);
        Mockito.when(shopDao.getFreight(Mockito.anyLong(),Mockito.anyLong(),Mockito.any())).thenReturn(new InternalReturnObject<>(freightDto));


        this.mockMvc.perform(MockMvcRequestBuilders.post("/orders").header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(body.getBytes("utf-8")))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }
    @Test
    public void retrieveOrderTest1() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/{shopId}/orders/{id}", 1L, 2L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderItems[0].quantity", is(3)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateOrderTest1() throws Exception{
        String body="{\"consignee\": \"张三\",\"regionId\": 1,\"address\": \"厦门大学\",\"mobile\": \"12345678911\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(UPDATEORDER,2L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(body.getBytes("utf-8")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())));
    }

    @Test
    public void cancelOrderTest1() throws Exception{
        //调用支付模块获取支付单
        FullPayTransDto fullPayTransDto= new FullPayTransDto();
        fullPayTransDto.setId(1L);
        fullPayTransDto.setAmount(100L);
        fullPayTransDto.setDivAmount(20L);
        Mockito.when(paymentDao.getPayment(Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(fullPayTransDto));
        //调用支付模块退款
        RefundTransDto refundTransDto= new RefundTransDto();
        refundTransDto.setId(1L);
        Mockito.when(paymentDao.createRefund(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(refundTransDto));
        OrderPayment orderPayment= new OrderPayment();
        orderPayment.setId(1L);
        orderPayment.setPaymentId(1L);
        List<OrderPayment> orderPaymentList=new ArrayList<>();
        orderPaymentList.add(orderPayment);
        Mockito.when(orderPaymentDao.retrieveByOrderId((Mockito.any()))).thenReturn(orderPaymentList);
        Mockito.when(orderCancelRocketMQTemplate.sendMessageInTransaction(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new TransactionSendResult());
        this.mockMvc.perform(MockMvcRequestBuilders.delete(UPDATEORDER,2L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())));
    }

    @Test
    public void payOrderTest1() throws Exception{
        //调用支付模块获取支付渠道
        ShopChannelDto shopChannelDto = new ShopChannelDto();
        shopChannelDto.setId(1L);
        Mockito.when(paymentDao.getShopChannel(Mockito.any(), Mockito.any())).thenReturn(shopChannelDto);
        //调用支付模块完成支付，获取支付单
        SimplePayTransDto simplePayTransDto = new SimplePayTransDto();
        simplePayTransDto.setId(1L);
        simplePayTransDto.setPrepayId("123456");
        Mockito.when(paymentDao.createPayment(Mockito.any())).thenReturn(new InternalReturnObject<>(simplePayTransDto));
        Mockito.when(orderPayRocketMQTemplate.sendMessageInTransaction(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new TransactionSendResult());
        String body = "{\"point\": 200,\"shopChannel\": 1,\"coupons\":[]}";
        this.mockMvc.perform(MockMvcRequestBuilders.post("/orders/{id}/pay", 2L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(body.getBytes("utf-8")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())));
    }

    @Test
    public void retrieveOrdersTest() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/{shopId}/orders", 1L)
                        .header("authorization",adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.page", is(1)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateOrderMessage() throws Exception{
        String body = "{\"message\": \"test\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/orders/{id}",1L,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(body.getBytes("utf-8")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void delOrdersTest01() throws Exception {
//        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/shops/{shopId}/orders/{id}", 1L,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateOrderStatus() throws Exception{

        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/orders/{id}/confirm",1L,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void retrieveOrdersStatusTest1() throws Exception {
        String path = "/orders/states";
        this.mockMvc.perform(MockMvcRequestBuilders.get(path)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())));
    }

    @Test
    public void retrieveCustomerOrdersSummaryTest1() throws Exception {
        String path = "/orders";
        this.mockMvc.perform(MockMvcRequestBuilders.get(path)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));
    }

    @Test
    public void retrieveOrderTestController1() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.get("/orders/{id}",  2L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderItems[0].quantity", is(3)))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 顾客修改订单，成功
     */
    @Test
    public void updateOrderByCustomerTest1(){
        UpdateOrderVo orderVo=new UpdateOrderVo();
        orderVo.setConsignee("张三");
        orderVo.setAddress("厦门大学");
        orderVo.setMobile("12345678911");
        orderVo.setRegionId(1L);
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        orderService.updateOrderByCustomer(2L,orderVo,user);
    }

    /**
     * 顾客修改订单，订单状态不可修改
     */
    @Test
    public void updateOrderByCustomerTest2(){
        UpdateOrderVo orderVo=new UpdateOrderVo();
        orderVo.setConsignee("张三");
        orderVo.setAddress("厦门大学");
        orderVo.setMobile("12345678911");
        orderVo.setRegionId(1L);
        UserDto user = new UserDto();
        user.setId(Long.valueOf(1));
        user.setName("test1");
        user.setUserLevel(1);
        assertThrows(BusinessException.class,()->orderService.updateOrderByCustomer(1L,orderVo,user)) ;
    }
    /**
     * 顾客修改订单，订单不为该顾客的
     */
    @Test
    public void updateOrderByCustomerTest3(){
        UpdateOrderVo orderVo=new UpdateOrderVo();
        orderVo.setConsignee("张三");
        orderVo.setAddress("厦门大学");
        orderVo.setMobile("12345678911");
        orderVo.setRegionId(1L);
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        assertThrows(BusinessException.class,()->orderService.updateOrderByCustomer(1L,orderVo,user)) ;
    }

    /**
     * 顾客取消订单测试，成功
     */
    @Test
    public void cancelOrderByCustomerTest1(){
        //调用支付模块获取支付单
        FullPayTransDto fullPayTransDto= new FullPayTransDto();
        fullPayTransDto.setId(1L);
        fullPayTransDto.setAmount(100L);
        fullPayTransDto.setDivAmount(20L);
        Mockito.when(paymentDao.getPayment(Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(fullPayTransDto));
        //调用支付模块退款
        RefundTransDto refundTransDto= new RefundTransDto();
        refundTransDto.setId(1L);
        Mockito.when(paymentDao.createRefund(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(refundTransDto));
        OrderPayment orderPayment= new OrderPayment();
        orderPayment.setId(1L);
        orderPayment.setPaymentId(1L);
        List<OrderPayment> orderPaymentList=new ArrayList<>();
        orderPaymentList.add(orderPayment);
        Mockito.when(orderPaymentDao.retrieveByOrderId((Mockito.any()))).thenReturn(orderPaymentList);
        Mockito.when(orderCancelRocketMQTemplate.sendMessageInTransaction(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new TransactionSendResult());
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        orderService.cancelOrderByCustomer(2L,user);
    }

    /**
     * 顾客取消订单，订单非顾客本人的
     */
    @Test
    public void cancelOrderByCustomerTest2(){
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        assertThrows(BusinessException.class,()->orderService.cancelOrderByCustomer(1L,user));
    }

    /**
     * 顾客取消订单，订单状态不可取消
     */
    @Test
    public void cancelOrderByCustomerTest3(){
        UserDto user = new UserDto();
        user.setId(Long.valueOf(1));
        user.setName("test1");
        user.setUserLevel(1);
        assertThrows(BusinessException.class,()->orderService.cancelOrderByCustomer(1L,user));
    }

    @Test
    public void retrieveOrderDaoTest1(){
        UserDto user = new UserDto();
        user.setId(Long.valueOf("1"));
        user.setName("test1");
        OrderDto orderDto = orderService.retrieveOrder(1L, 1L, user);
        assertEquals("长通型牛奶杯", orderDto.getOrderItems().get(0).getName());
    }

    /**
     * 顾客支付订单，成功
     */
    @Test
    public void payOrderByCustomerTest1(){
        //调用支付模块获取支付渠道
        ShopChannelDto shopChannelDto = new ShopChannelDto();
        shopChannelDto.setId(1L);
        Mockito.when(paymentDao.getShopChannel(Mockito.any(), Mockito.any())).thenReturn(shopChannelDto);
        //调用支付模块完成支付，获取支付单
        SimplePayTransDto simplePayTransDto = new SimplePayTransDto();
        simplePayTransDto.setId(1L);
        simplePayTransDto.setPrepayId("123456");
        Mockito.when(paymentDao.createPayment(Mockito.any())).thenReturn(new InternalReturnObject<>(simplePayTransDto));
        Mockito.when(orderPayRocketMQTemplate.sendMessageInTransaction(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new TransactionSendResult());
        PayVo payVo = new PayVo();
        payVo.setPoint(200L);
        payVo.setShopChannel(1L);
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        orderService.payOrderByCustomer(200L, payVo, user);
    }

    /**
     * 顾客支付订单，订单状态不可支付
     */
    @Test
    public void payOrderByCustomerTest2(){
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("test2");
        PayVo payVo = new PayVo();
        assertThrows(BusinessException.class, () -> orderService.payOrderByCustomer(1L, payVo, user));
    }

    /**
     * 顾客支付订单，该订单顾客非该顾客
     */
    @Test
    public void payOrderByCustomerTest3(){
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("test2");
        PayVo payVo = new PayVo();
        assertThrows(BusinessException.class, () -> orderService.payOrderByCustomer(200L, payVo, user));
    }


    @Test
    public void retrieveAllOrderStatusTest(){
        List<OrderStatusDto> list = (List<OrderStatusDto>) orderService.retrieveAllOrderStatus().getData();
        assertEquals(25212,list.size());
        assertEquals(300,list.get(0).getCode());
        assertEquals("已完成",list.get(0).getName());
    }


    @Test
    public void refundOrderTest(){
        //调用支付模块获取支付单
        FullPayTransDto fullPayTransDto= new FullPayTransDto();
        fullPayTransDto.setId(1L);
        fullPayTransDto.setAmount(100L);
        fullPayTransDto.setDivAmount(20L);
        Mockito.when(paymentDao.getPayment(Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(fullPayTransDto));
        //调用支付模块退款
        RefundTransDto refundTransDto= new RefundTransDto();
        refundTransDto.setId(1L);
        Mockito.when(paymentDao.createRefund(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(refundTransDto));
        OrderPayment orderPayment=new OrderPayment();
        orderPayment.setOrderId(6L);
        orderPayment.setPaymentId(1L);
        Order order=new Order();
        order.setShopId(1L);
        order.setId(1L);
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        orderService.refundOrder(orderPayment,order,user);
    }

    @Test
    public void payOrderTest(){
        ShopChannelDto shopChannelDto = new ShopChannelDto();
        shopChannelDto.setId(1L);
        Mockito.when(paymentDao.getShopChannel(Mockito.any(), Mockito.any())).thenReturn(shopChannelDto);
        //调用支付模块完成支付，获取支付单
        SimplePayTransDto simplePayTransDto = new SimplePayTransDto();
        simplePayTransDto.setId(1L);
        simplePayTransDto.setPrepayId("123456");
        Mockito.when(paymentDao.createPayment(Mockito.any())).thenReturn(new InternalReturnObject<>(simplePayTransDto));
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        Order order = new Order();
        order.setId(200L);
        order.setShopId(1L);
        order.setCustomerId(2L);
        order.setOriginPrice(500L);
        order.setDiscountPrice(0L);
        order.setPoint(100L);
        order.setExpressFee(67L);
        orderService.payOrder(order, 1L, user);
    }


    @Test
    public void updateOrderMessageTest1(){
        UserDto userDto=null;
        assertThrows(BusinessException.class,()->orderService.updateOrderMessage(1L,1L,null,userDto));
    }

    @Test
    public void updateOrderMessageTest2(){
        UserDto user=new UserDto();
        user.setId(Long.valueOf("1"));
        user.setName("test1");
        String message="test message";
        orderService.updateOrderMessage(1L,1L,message,user);
    }


    @Test
    public void retrieveOrdersTest2(){
        UserDto user=new UserDto();
        user.setId(Long.valueOf("1"));
        user.setName("test1");

        PageDto<OrderSummaryDto> ret=orderService.retrieveOrders(1L,1L,null,null,null,1,10,user);
        assertEquals(1L,ret.getList().get(0).getId());
        System.out.println(ret.getList().get(0));
    }

    @Test
    public void retrieveCustomerOrdersTest1(){
        PageDto<OrderSummaryDto> ret = orderService.retrieveCustomerOrders(300, null, null, null, 1, 3, new UserDto(1L, "xxr", 1L, 1));
        assertEquals(1L,ret.getList().get(0).getId());
        assertEquals(300,ret.getList().get(0).getStatus());
    }
    @Test
    public void delOrderTest01() {
        UserDto user = new UserDto();
        user.setName("test1");
        user.setUserLevel(1);
        ReturnObject returnObject = orderService.delOrders(user, 1L, 1L);
        assertEquals(ReturnNo.OK.getErrNo(),returnObject.getErrno());
    }

    @Test
    public void updateOrderStatusTest1(){

        UserDto user = new UserDto();
        user.setId(Long.valueOf(1));
        user.setName("test1");
        // OrderDto ret = orderService.updateOrderStatus(user,1L,1L);
        orderService.updateOrderStatus(user,1L,1L);
    }

    @Test
    public void updateOrderStatusTest2(){
        UserDto userDto=null;
        assertThrows(BusinessException.class,()->orderService.updateOrderStatus(userDto,1L,1L));
    }

    /**
     * 保存订单
     * 0,普通商品,牛奶，最大可售100，单次最多2，价格5元，只参与一个优惠活动
     */
    @Test
    public void saveOrderTest01(){
        IdNameDto product = new IdNameDto(1L,"牛奶");
        IdNameTypeDto shop = new IdNameTypeDto(1L,"金典官方旗舰店", (byte) 1);
        LocalDateTime saleBegin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
        LocalDateTime saleEnd = LocalDateTime.parse("2024-01-01T12:12:12", DATE_TIME_FORMATTER);
        List<IdNameTypeDto> actList = new ArrayList<>();
        actList.add(new IdNameTypeDto(1L,"优惠活动",(byte)1));
        OnsaleDto onsaleDto = new OnsaleDto(1L,shop,product, 5000L,saleBegin,saleEnd,100,2, (byte) 0,actList);
        Mockito.when(goodsDao.getOnsaleById(Mockito.anyLong(),Mockito.anyLong())).thenReturn(new InternalReturnObject<>(onsaleDto));

        IdNameDto template = new IdNameDto(1L,"一般运费模板");
        FullProductDto fullProductDto = new FullProductDto(1L,"牛奶",5L,250L,template);
        Mockito.when(goodsDao.getProductById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(fullProductDto));

        LocalDateTime actBegin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
        LocalDateTime actEnd = LocalDateTime.parse("2024-01-01T12:12:12", DATE_TIME_FORMATTER);
        ActivityDto activityDto = new ActivityDto(1L,"优惠活动",(byte)0);
        CouponDto couponDto = new CouponDto(activityDto,0,actBegin,actEnd);
        Mockito.when(customerDao.getCouponsById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(couponDto));

        FreightDto freightDto = new FreightDto(5,null);
        Mockito.when(shopDao.getFreight(Mockito.anyLong(),Mockito.anyLong(),Mockito.any())).thenReturn(new InternalReturnObject<>(freightDto));

        Map<Long,List<OrderItem>> packs = new HashMap<>();
        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = new OrderItem(1L,2L,"amz",null,null,null,null,1L,1L,1,5000L,0L,0L,"牛奶",1L,1L,null);
        orderItem.setProductId(1L);
        orderItem.setFreightTemplateId(1L);
        orderItem.setWeight(250L);
        orderItemList.add(orderItem);
        packs.put(1L,orderItemList);

        ConsigneeDto consigneeDto = new ConsigneeDto("amz","郊区",1L,"13017389877");
        UserDto userDto = new UserDto(1L,"amz",-100L,1);
        orderService.saveOrder(packs,consigneeDto,"请发顺丰",userDto);

    }



    @Test
    public void createOrderTest01(){
        IdNameDto product = new IdNameDto(1L,"牛奶");
        IdNameTypeDto shop = new IdNameTypeDto(1L,"金典官方旗舰店", (byte) 1);
        LocalDateTime saleBegin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
        LocalDateTime saleEnd = LocalDateTime.parse("2024-01-01T12:12:12", DATE_TIME_FORMATTER);
        List<IdNameTypeDto> actList = new ArrayList<>();
        actList.add(new IdNameTypeDto(1L,"优惠活动",(byte)1));
        OnsaleDto onsaleDto = new OnsaleDto(1L,shop,product, 5000L,saleBegin,saleEnd,100,2, (byte) 0,actList);
        Mockito.when(goodsDao.getOnsaleById(Mockito.anyLong(),Mockito.anyLong())).thenReturn(new InternalReturnObject<>(onsaleDto));

        IdNameDto template = new IdNameDto(1L,"一般运费模板");
        FullProductDto fullProductDto = new FullProductDto(1L,"牛奶",5L,250L,template);
        Mockito.when(goodsDao.getProductById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(fullProductDto));

        LocalDateTime actBegin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
        LocalDateTime actEnd = LocalDateTime.parse("2024-01-01T12:12:12", DATE_TIME_FORMATTER);
        ActivityDto activityDto = new ActivityDto(1L,"优惠活动",(byte)0);
        CouponDto couponDto = new CouponDto(activityDto,0,actBegin,actEnd);
        Mockito.when(customerDao.getCouponsById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(couponDto));

        FreightDto freightDto = new FreightDto(5,null);
        Mockito.when(shopDao.getFreight(Mockito.anyLong(),Mockito.anyLong(),Mockito.any())).thenReturn(new InternalReturnObject<>(freightDto));

        OrderItemDto orderItemDto = new OrderItemDto(1L,1,1L,1L);
        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        orderItemDtos.add(orderItemDto);

        ConsigneeDto consigneeDto = new ConsigneeDto("amz","郊区",1L,"13029302930");

        Mockito.when(orderCreateRocketMQTemplate.sendMessageInTransaction(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new TransactionSendResult());

        UserDto userDto = new UserDto(1L,"amz",1L,1);
        orderService.createOrder(orderItemDtos,consigneeDto,"请尽快发货",userDto);

    }

    /**
     * 购买超过最大限额的数量
     */
    @Test
    public void packOrderTest01(){
        IdNameDto product = new IdNameDto(1L,"牛奶");
        IdNameTypeDto shop = new IdNameTypeDto(1L,"金典官方旗舰店", (byte) 1);
        LocalDateTime saleBegin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
        LocalDateTime saleEnd = LocalDateTime.parse("2024-01-01T12:12:12", DATE_TIME_FORMATTER);
        List<IdNameTypeDto> actList = new ArrayList<>();
        actList.add(new IdNameTypeDto(1L,"优惠活动",(byte)1));
        OnsaleDto onsaleDto = new OnsaleDto(1L,shop,product, 5000L,saleBegin,saleEnd,100,2, (byte) 0,actList);
        Mockito.when(goodsDao.getOnsaleById(Mockito.anyLong(),Mockito.anyLong())).thenReturn(new InternalReturnObject<>(onsaleDto));

        IdNameDto template = new IdNameDto(1L,"一般运费模板");
        FullProductDto fullProductDto = new FullProductDto(1L,"牛奶",5L,250L,template);
        Mockito.when(goodsDao.getProductById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(fullProductDto));

        LocalDateTime actBegin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
        LocalDateTime actEnd = LocalDateTime.parse("2024-01-01T12:12:12", DATE_TIME_FORMATTER);
        ActivityDto activityDto = new ActivityDto(1L,"优惠活动",(byte)0);
        CouponDto couponDto = new CouponDto(activityDto,0,actBegin,actEnd);
        Mockito.when(customerDao.getCouponsById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(couponDto));

        FreightDto freightDto = new FreightDto(5,null);
        Mockito.when(shopDao.getFreight(Mockito.anyLong(),Mockito.anyLong(),Mockito.any())).thenReturn(new InternalReturnObject<>(freightDto));

        OrderItemDto orderItemDto = new OrderItemDto(1L,3,1L,1L);
        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        orderItemDtos.add(orderItemDto);

        UserDto userDto = new UserDto(1L,"amz",1L,1);

        assertThrows(BusinessException.class,()->orderService.packOrder(orderItemDtos,userDto));

    }

    /**
     * 购买合适数量商品
     */
    @Test
    public void packOrderTest02(){
        IdNameDto product = new IdNameDto(1L,"牛奶");
        IdNameTypeDto shop = new IdNameTypeDto(1L,"金典官方旗舰店", (byte) 1);
        LocalDateTime saleBegin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
        LocalDateTime saleEnd = LocalDateTime.parse("2024-01-01T12:12:12", DATE_TIME_FORMATTER);
        List<IdNameTypeDto> actList = new ArrayList<>();
        actList.add(new IdNameTypeDto(1L,"优惠活动",(byte)1));
        OnsaleDto onsaleDto = new OnsaleDto(1L,shop,product, 5000L,saleBegin,saleEnd,100,2, (byte) 0,actList);
        Mockito.when(goodsDao.getOnsaleById(Mockito.anyLong(),Mockito.anyLong())).thenReturn(new InternalReturnObject<>(onsaleDto));

        IdNameDto template = new IdNameDto(1L,"一般运费模板");
        FullProductDto fullProductDto = new FullProductDto(1L,"牛奶",5L,250L,template);
        Mockito.when(goodsDao.getProductById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(fullProductDto));

        LocalDateTime actBegin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
        LocalDateTime actEnd = LocalDateTime.parse("2024-01-01T12:12:12", DATE_TIME_FORMATTER);
        ActivityDto activityDto = new ActivityDto(1L,"优惠活动",(byte)0);
        CouponDto couponDto = new CouponDto(activityDto,0,actBegin,actEnd);
        Mockito.when(customerDao.getCouponsById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(couponDto));

        FreightDto freightDto = new FreightDto(5,null);
        Mockito.when(shopDao.getFreight(Mockito.anyLong(),Mockito.anyLong(),Mockito.any())).thenReturn(new InternalReturnObject<>(freightDto));

        OrderItemDto orderItemDto = new OrderItemDto(1L,2,1L,1L);
        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        orderItemDtos.add(orderItemDto);

        UserDto userDto = new UserDto(1L,"amz",1L,1);
        Map<Long, List<OrderItem>> map = orderService.packOrder(orderItemDtos, userDto);

        assertEquals("牛奶",map.get(1L).get(0).getName());
        System.out.println(map.get(1L).get(0));
    }

    @Test
    public void findByIdTest1(){
        Order order = new Order();
        order.setId(Long.valueOf(1));
        order.setCustomerId(Long.valueOf(1));
        order.setShopId(Long.valueOf(1));
        order.setOrderSn("2016102361242");
        order.setAddress("人民北路");

        Order ret = orderDao.findById(Long.valueOf(1));

        assertThat(ret.getId()).isEqualTo(order.getId());
        assertThat(ret.getCustomerId()).isEqualTo(order.getCustomerId());
        assertThat(ret.getShopId()).isEqualTo(order.getShopId());
        assertThat(ret.getOrderSn()).isEqualTo(order.getOrderSn());
        assertThat(ret.getAddress()).isEqualTo(order.getAddress());
    }

    @Test
    public void findByShopIdAndOrderIdTest1(){
        Order order=orderDao.findByShopIdAndOrderId(1L,1L);
        assertThat(order.getCustomerId()).isEqualTo(1L);
        assertThat(order.getOrderSn()).isEqualTo("2016102361242");
        assertThat(order.getConsignee()).isEqualTo("赵永波");
    }

    @Test
    public void findByShopIdAndOrderIdTest2(){
        assertThrows(BusinessException.class,()->orderDao.findByShopIdAndOrderId(1L,3L));
    }

    @Test
    public void retrieveByShopIdTest1(){
        List<Order> ret=orderDao.retrieveByShopId(1L,null,null,null,null,1,10);
        System.out.println(ret.size());
        assertThat(ret.get(1).getOrderSn()).isEqualTo("2016102378405");
        assertThat(ret.get(2).getId()).isEqualTo(4L);
    }

    @Test
    public void retrieveByShopIdTest2(){
        List<Order> ret=orderDao.retrieveByShopId(1L,2L,null,null,null,1,10);
        assertThat(ret.get(0).getOrderSn()).isEqualTo("2016102378405");
        assertThat(ret.get(0).getConsignee()).isEqualTo("赵俊峻");
    }

    @Test
    public void retrieveByShopIdTest3(){
        List<Order> ret=orderDao.retrieveByShopId(1L,null,"2016102364965",null,null,1,10);
        assertThat(ret.get(0).getId()).isEqualTo(4L);
        assertThat(ret.get(0).getRegionId()).isEqualTo(2417L);
    }

    @Test
    public void retrieveByShopIdTest4(){
        List<Order> ret=orderDao.retrieveByShopId(1L,null,null, LocalDateTime.parse("2016-10-23T12:26:43"),null,1,10);
        assertThat(ret.get(0).getId()).isEqualTo(10L);
    }

    @Test
    public void retrieveByShopIdTest5(){
        List<Order> ret=orderDao.retrieveByShopId(1L,2L,"2016102378405",null,null,1,10);
        assertThat(ret.get(0).getConsignee()).isEqualTo("赵俊峻");
    }

    @Test
    public void retrieveByShopIdTest6(){
        assertThrows(BusinessException.class,()->orderDao.retrieveByShopId(1L,333333333L,null,null,null,1,10));
    }

    @Test
    public void createOrder(){
        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = new OrderItem(1L,2L,"amz",null,null,null,null,1L,1L,1,5000L,0L,0L,"牛奶",1L,1L,null);
        orderItem.setProductId(1L);
        orderItem.setFreightTemplateId(1L);
        orderItem.setWeight(250L);
        orderItemList.add(orderItem);
        Order order = new Order(1L,1L,"amz",null,null,null,null,1L,1L,"123",null,"amz",1L,"郊区","13017238291","请尽快发货",1L,1L,orderItemList);
        orderDao.createOrder(order);

    }

    @Test
    public void delByIdTest(){
        Order order= orderDao.findById(1L);
        orderDao.delById(order);
        assertThrows(BusinessException.class,()->orderDao.findById(1L)) ;
    }

    @Test
    public void retrieveByOrderIdTest(){
        List<OrderItem> orderItems= orderItemDao.retrieveByOrderId(1L).getList();
        Assertions.assertThat(orderItems.get(0).getId()).isEqualTo(11864L);
    }

    @Test
    public void retrieveByActivityIdTest(){
        List<OrderItem> orderItems= orderItemDao.retrieveByActivityId(1L).getList();
    }

}
