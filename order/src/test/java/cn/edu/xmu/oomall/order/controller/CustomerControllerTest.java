//package cn.edu.xmu.oomall.order.controller;
//
//import cn.edu.xmu.javaee.core.exception.BusinessException;
//import cn.edu.xmu.javaee.core.model.InternalReturnObject;
//import cn.edu.xmu.javaee.core.model.ReturnNo;
//import cn.edu.xmu.javaee.core.util.JacksonUtil;
//import cn.edu.xmu.javaee.core.util.JwtHelper;
//import cn.edu.xmu.javaee.core.util.RedisUtil;
//import cn.edu.xmu.oomall.order.OrderApplication;
//import cn.edu.xmu.oomall.order.OrderTestApplication;
//import cn.edu.xmu.oomall.order.dao.OrderDao;
//import cn.edu.xmu.oomall.order.dao.OrderPaymentDao;
//import cn.edu.xmu.oomall.order.dao.bo.Order;
//import cn.edu.xmu.oomall.order.dao.bo.OrderPayment;
//import cn.edu.xmu.oomall.order.dao.openfeign.CustomerDao;
//import cn.edu.xmu.oomall.order.dao.openfeign.GoodsDao;
//import cn.edu.xmu.oomall.order.dao.openfeign.PaymentDao;
//import cn.edu.xmu.oomall.order.dao.openfeign.ShopDao;
//import cn.edu.xmu.oomall.order.dao.openfeign.dto.*;
//import cn.edu.xmu.oomall.order.service.OrderService;
//import cn.edu.xmu.oomall.order.service.rocketMQTemplate.OrderCancelRocketMQTemplate;
//import cn.edu.xmu.oomall.order.service.rocketMQTemplate.OrderCreateRocketMQTemplate;
//import cn.edu.xmu.oomall.order.service.rocketMQTemplate.OrderPayRocketMQTemplate;
//import org.apache.rocketmq.client.producer.TransactionSendResult;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import static cn.edu.xmu.javaee.core.model.Constants.DATE_TIME_FORMATTER;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.hamcrest.CoreMatchers.is;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@SpringBootTest(classes = OrderTestApplication.class)
//@Transactional
//@AutoConfigureMockMvc
//public class CustomerControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//
//    private static String adminToken;
//
//    private static final String UPDATEORDER = "/orders/{id}";
//
//    @MockBean
//    //@Qualifier("orderCancelRocketMQTemplate")
//    private OrderCancelRocketMQTemplate orderCancelRocketMQTemplate;
//
//    @MockBean
//    //@Qualifier("rocketMQTemplate")
//    private OrderCreateRocketMQTemplate orderCreateRocketMQTemplate;
//
//    @MockBean
//    private OrderPayRocketMQTemplate orderPayRocketMQTemplate;
//
//    @MockBean
//    private RedisUtil redisUtil;
//
//    @Qualifier("rocketMQTemplate")
//    private RocketMQTemplate rocketMQTemplate;
//
//    @MockBean
//    private PaymentDao paymentDao;
//
//    @MockBean
//    private OrderPaymentDao orderPaymentDao;
//
//    @MockBean
//    private GoodsDao goodsDao;
//
//    @MockBean
//    private ShopDao shopDao;
//
//    @MockBean
//    private CustomerDao customerDao;
//
//    /**
//     * 用户1没有未完成的订单，无法测试成功路径，所以这里id改成2了，有需要改回1的同学可能得重新建一个ControllerTest
//     */
//    @BeforeAll
//    public static void setup(){
//        JwtHelper jwtHelper = new JwtHelper();
//        adminToken = jwtHelper.createToken(2L, "13088admin", 0L, 1, 3600);
//    }
//    @Test
//    public void updateOrderTest1() throws Exception{
//        String body="{\"consignee\": \"张三\",\"regionId\": 1,\"address\": \"厦门大学\",\"mobile\": \"12345678911\"}";
//        this.mockMvc.perform(MockMvcRequestBuilders.put(UPDATEORDER,2L)
//                        .header("authorization", adminToken)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(body.getBytes("utf-8")))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())));
//    }
//
//    @Test
//    public void cancelOrderTest1() throws Exception{
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
//        this.mockMvc.perform(MockMvcRequestBuilders.delete(UPDATEORDER,2L)
//                        .header("authorization", adminToken)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())));
//    }
//
//    @Test
//    public void payOrderTest1() throws Exception{
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
//        String body = "{\"point\": 200,\"shopChannel\": 1,\"coupons\":[]}";
//        this.mockMvc.perform(MockMvcRequestBuilders.post("/orders/{id}/pay", 2L)
//                        .header("authorization", adminToken)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(body.getBytes("utf-8")))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())));
//    }
//
//    @Test
//    public void delOrdersTest01() throws Exception {
////        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
//
//        this.mockMvc.perform(MockMvcRequestBuilders.delete("/shops/{shopId}/orders/{id}", 1L,1L)
//                        .header("authorization", adminToken)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
//                .andDo(MockMvcResultHandlers.print());
//    }
//
//    @Test
//    public void updateOrderStatus() throws Exception{
//
//        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/orders/{id}/confirm",1L,1L)
//                        .header("authorization", adminToken)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
//                .andDo(MockMvcResultHandlers.print());
//    }
//
//    @Test
//    public void createOrderTest() throws Exception {
//        String body = "{\"items\": [{\"couponId\":1,\"onsaleId\": 1,\"quantity\": 2, \"actId\": 1}],\"consignee\": \"amz\",\"mobile\": \"13017382938\",\"regionId\": 1,\"address\": \"郊区\",\"message\": \"尽快发货\"}";
//        Mockito.when(orderCreateRocketMQTemplate.sendMessageInTransaction(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new TransactionSendResult());
//
//        IdNameDto product = new IdNameDto(1L,"牛奶");
//        IdNameTypeDto shop = new IdNameTypeDto(1L,"金典官方旗舰店", (byte) 1);
//        LocalDateTime saleBegin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
//        LocalDateTime saleEnd = LocalDateTime.parse("2024-01-01T12:12:12", DATE_TIME_FORMATTER);
//        List<IdNameTypeDto> actList = new ArrayList<>();
//        actList.add(new IdNameTypeDto(1L,"优惠活动",(byte)1));
//        OnsaleDto onsaleDto = new OnsaleDto(1L,shop,product, 5000L,saleBegin,saleEnd,100,2, (byte) 0,actList);
//        Mockito.when(goodsDao.getOnsaleById(Mockito.anyLong(),Mockito.anyLong())).thenReturn(new InternalReturnObject<>(onsaleDto));
//
//        IdNameDto template = new IdNameDto(1L,"一般运费模板");
//        FullProductDto fullProductDto = new FullProductDto(1L,"牛奶",5L,250L,template);
//        Mockito.when(goodsDao.getProductById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(fullProductDto));
//
//        LocalDateTime actBegin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
//        LocalDateTime actEnd = LocalDateTime.parse("2024-01-01T12:12:12", DATE_TIME_FORMATTER);
//        ActivityDto activityDto = new ActivityDto(1L,"优惠活动",(byte)0);
//        CouponDto couponDto = new CouponDto(activityDto,0,actBegin,actEnd);
//        Mockito.when(customerDao.getCouponsById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(couponDto));
//
//        FreightDto freightDto = new FreightDto(5,null);
//        Mockito.when(shopDao.getFreight(Mockito.anyLong(),Mockito.anyLong(),Mockito.any())).thenReturn(new InternalReturnObject<>(freightDto));
//
//
//        this.mockMvc.perform(MockMvcRequestBuilders.post("/orders").header("authorization", adminToken)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(body.getBytes("utf-8")))
//                .andExpect(MockMvcResultMatchers.status().isCreated());
//    }
//
//    @Test
//    public void retrieveOrdersStatusTest1() throws Exception {
//        String path = "/orders/states";
//        this.mockMvc.perform(MockMvcRequestBuilders.get(path)
//                        .header("authorization", adminToken)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())));
//    }
//
//    @Test
//    public void retrieveCustomerOrdersSummaryTest1() throws Exception {
//        String path = "/orders";
//        this.mockMvc.perform(MockMvcRequestBuilders.get(path)
//
//                        .header("authorization", adminToken)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id", is(1)))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status", is(300)));
//    }
//
//    @Test
//    public void retrieveOrderTest1() throws Exception{
//        this.mockMvc.perform(MockMvcRequestBuilders.get("/orders/{id}",  2L)
//                        .header("authorization", adminToken)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderItems[0].quantity", is(3)))
//                .andDo(MockMvcResultHandlers.print());
//    }
//}
