//package cn.edu.xmu.oomall.order.service;
//
//import cn.edu.xmu.javaee.core.exception.BusinessException;
//import cn.edu.xmu.javaee.core.model.InternalReturnObject;
//import cn.edu.xmu.javaee.core.model.dto.UserDto;
//import cn.edu.xmu.oomall.order.OrderTestApplication;
//import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
//import cn.edu.xmu.oomall.order.dao.openfeign.CustomerDao;
//import cn.edu.xmu.oomall.order.dao.openfeign.GoodsDao;
//import cn.edu.xmu.oomall.order.dao.openfeign.ShopDao;
//import cn.edu.xmu.oomall.order.dao.openfeign.dto.*;
//import cn.edu.xmu.oomall.order.dao.openfeign.dto.ActivityDto;
//import cn.edu.xmu.oomall.order.dao.openfeign.dto.CouponDto;
//import cn.edu.xmu.oomall.order.service.dto.*;
//import cn.edu.xmu.oomall.order.service.rocketMQTemplate.OrderCreateRocketMQTemplate;
//import org.apache.rocketmq.client.producer.TransactionSendResult;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//import static cn.edu.xmu.javaee.core.model.Constants.DATE_TIME_FORMATTER;
//
//@SpringBootTest(classes = OrderTestApplication.class)
//@Transactional
//public class OrderService3Test {
//    @Autowired
//    private OrderService orderService;
//    @MockBean
//    private OrderCreateRocketMQTemplate orderCreateRocketMQTemplate;
//
//    @MockBean
//    private GoodsDao goodsDao;
//
//    @MockBean
//    private CustomerDao customerDao;
//
//    @MockBean
//    private ShopDao shopDao;
//
//    /**
//     * 保存订单
//     * 0,普通商品,牛奶，最大可售100，单次最多2，价格5元，只参与一个优惠活动
//     */
//    @Test
//    public void saveOrderTest01(){
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
//        Map<Long,List<OrderItem>> packs = new HashMap<>();
//        List<OrderItem> orderItemList = new ArrayList<>();
//        OrderItem orderItem = new OrderItem(1L,2L,"amz",null,null,null,null,1L,1L,1,5000L,0L,0L,"牛奶",1L,1L,null);
//        orderItem.setProductId(1L);
//        orderItem.setFreightTemplateId(1L);
//        orderItem.setWeight(250L);
//        orderItemList.add(orderItem);
//        packs.put(1L,orderItemList);
//
//        ConsigneeDto consigneeDto = new ConsigneeDto("amz","郊区",1L,"13017389877");
//        UserDto userDto = new UserDto(1L,"amz",-100L,1);
//        orderService.saveOrder(packs,consigneeDto,"请发顺丰",userDto);
//
//    }
//
//
//
//    @Test
//    public void createOrderTest01(){
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
//        OrderItemDto orderItemDto = new OrderItemDto(1L,1,1L,1L);
//        List<OrderItemDto> orderItemDtos = new ArrayList<>();
//        orderItemDtos.add(orderItemDto);
//
//        ConsigneeDto consigneeDto = new ConsigneeDto("amz","郊区",1L,"13029302930");
//
//        Mockito.when(orderCreateRocketMQTemplate.sendMessageInTransaction(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new TransactionSendResult());
//
//        UserDto userDto = new UserDto(1L,"amz",1L,1);
//        orderService.createOrder(orderItemDtos,consigneeDto,"请尽快发货",userDto);
//
//    }
//
//    /**
//     * 购买超过最大限额的数量
//     */
//    @Test
//    public void packOrderTest01(){
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
//        OrderItemDto orderItemDto = new OrderItemDto(1L,3,1L,1L);
//        List<OrderItemDto> orderItemDtos = new ArrayList<>();
//        orderItemDtos.add(orderItemDto);
//
//        UserDto userDto = new UserDto(1L,"amz",1L,1);
//
//        assertThrows(BusinessException.class,()->orderService.packOrder(orderItemDtos,userDto));
//
//    }
//
//    /**
//     * 购买合适数量商品
//     */
//    @Test
//    public void packOrderTest02(){
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
//        OrderItemDto orderItemDto = new OrderItemDto(1L,2,1L,1L);
//        List<OrderItemDto> orderItemDtos = new ArrayList<>();
//        orderItemDtos.add(orderItemDto);
//
//        UserDto userDto = new UserDto(1L,"amz",1L,1);
//        Map<Long, List<OrderItem>> map = orderService.packOrder(orderItemDtos, userDto);
//
//        assertEquals("牛奶",map.get(1L).get(0).getName());
//        System.out.println(map.get(1L).get(0));
//    }
//
//
//
//}
