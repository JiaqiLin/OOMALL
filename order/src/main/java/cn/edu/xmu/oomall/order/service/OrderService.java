//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.Common;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.order.controller.vo.OrderVo;
import cn.edu.xmu.oomall.order.controller.vo.PayVo;
import cn.edu.xmu.oomall.order.controller.vo.UpdateOrderVo;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.dao.OrderItemDao;
import cn.edu.xmu.oomall.order.dao.OrderPaymentDao;
import cn.edu.xmu.oomall.order.dao.OrderRefundDao;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
import cn.edu.xmu.oomall.order.dao.bo.OrderPayment;
import cn.edu.xmu.oomall.order.dao.bo.OrderRefund;
import cn.edu.xmu.oomall.order.dao.openfeign.CustomerDao;
import cn.edu.xmu.oomall.order.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.order.dao.openfeign.PaymentDao;
import cn.edu.xmu.oomall.order.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.*;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.CouponDto;
import cn.edu.xmu.oomall.order.service.dto.*;
import cn.edu.xmu.oomall.order.service.dto.ActivityDto;
import cn.edu.xmu.oomall.order.service.dto.PackDto;
import cn.edu.xmu.oomall.order.service.responsibilityChain.DiscountPriceHandler;
import cn.edu.xmu.oomall.order.service.responsibilityChain.ExpressFeeHandler;
import cn.edu.xmu.oomall.order.service.responsibilityChain.OriginPriceHandler;
import cn.edu.xmu.oomall.order.service.rocketMQTemplate.OrderCancelRocketMQTemplate;
import cn.edu.xmu.oomall.order.service.rocketMQTemplate.OrderCreateRocketMQTemplate;
import cn.edu.xmu.oomall.order.service.rocketMQTemplate.OrderPayRocketMQTemplate;
import com.github.pagehelper.PageInfo;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;
import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class OrderService {

    @Value("${oomall.order.server-num}")
    private int serverNum;

    private OriginPriceHandler originPriceHandler;

    private DiscountPriceHandler discountPriceHandler;

    private ExpressFeeHandler expressFeeHandler;

    private GoodsDao goodsDao;

    private CustomerDao customerDao;

    private OrderDao orderDao;

    private OrderItemDao orderItemDao;

    private ShopDao shopDao;

    private RocketMQTemplate rocketMQTemplate;

    private OrderRefundDao orderRefundDao;

    private OrderPaymentDao orderPaymentDao;

    private PaymentDao paymentDao;

//    @Resource(name = "orderCancelRocketMQTemplate")
//    private OrderCancelRocketMQTemplate orderCancelRocketMQTemplate;

    private OrderCancelRocketMQTemplate orderCancelRocketMQTemplate;

//    @Resource(name="orderCreateRocketMQTemplate")
    private OrderCreateRocketMQTemplate orderCreateRocketMQTemplate;

    private OrderPayRocketMQTemplate orderPayRocketMQTemplate;

    @Autowired
    public OrderService(GoodsDao goodsDao, OrderDao orderDao, CustomerDao customerDao,
                        OrderItemDao orderItemDao, ShopDao shopDao,
                        RocketMQTemplate rocketMQTemplate,OriginPriceHandler originPriceHandler,
                        DiscountPriceHandler discountPriceHandler,ExpressFeeHandler expressFeeHandler,
                        OrderPaymentDao orderPaymentDao,OrderRefundDao orderRefundDao,PaymentDao paymentDao,
                        OrderCancelRocketMQTemplate orderCancelRocketMQTemplate,
                        OrderCreateRocketMQTemplate orderCreateRocketMQTemplate,
                        OrderPayRocketMQTemplate orderPayRocketMQTemplate
    ) {
        this.goodsDao = goodsDao;
        this.orderDao = orderDao;
        this.rocketMQTemplate = rocketMQTemplate;
        this.customerDao=customerDao;
        this.orderItemDao=orderItemDao;
        this.shopDao=shopDao;
        this.orderRefundDao=orderRefundDao;
        this.orderPaymentDao=orderPaymentDao;
        this.paymentDao=paymentDao;
        this.orderCancelRocketMQTemplate=orderCancelRocketMQTemplate;
        this.orderCreateRocketMQTemplate=orderCreateRocketMQTemplate;
        this.orderPayRocketMQTemplate=orderPayRocketMQTemplate;
        this.discountPriceHandler=discountPriceHandler;
        this.originPriceHandler=originPriceHandler;
        this.expressFeeHandler=expressFeeHandler;
        discountPriceHandler.setNext(originPriceHandler);
        originPriceHandler.setNext(expressFeeHandler);
    }

    @Transactional
    public Map<Long, List<OrderItem>> packOrder(List<OrderItemDto> items, UserDto customer){
        Map<Long, List<OrderItem>> packs = new HashMap<>();
        //1、将orderItemDto（onsaleId,actId,couponId,quantity）转化为orderItem

        items.stream().forEach(item -> {
            OnsaleDto onsaleDto = this.goodsDao.getOnsaleById(PLATFORM, item.getOnsaleId()).getData();
            OrderItem orderItem = OrderItem.builder().onsaleId(onsaleDto.getId()).price(onsaleDto.getPrice()).name(onsaleDto.getProduct().getName()).build();
            orderItem.setQuantity(item.getQuantity());
            //onsaleId,price,name

            orderItem.setProductId(onsaleDto.getProduct().getId());
            //productId

            FullProductDto fullProductDto = this.goodsDao.getProductById(orderItem.getProductId()).getData();
            orderItem.setWeight(fullProductDto.getWeight());
            orderItem.setFreightTemplateId(fullProductDto.getTemplate().getId());
            //weight,templateId

            orderItem.setProductQuantity(onsaleDto.getQuantity());
            //quantity

            if (null != onsaleDto.getActList() && null != item.getActId()){
                //所属活动在商品的活动列表中
                if (onsaleDto.getActList().stream().filter(activity -> activity.getId() == item.getActId()).count()  > 0){
                    orderItem.setActId(item.getActId());
                    //actId
                    //查看优惠卷id所属的活动是否在onsale的活动列表中，并且优惠卷是有效的，才能设置到orderItem中
                    CouponDto couponDto = this.customerDao.getCouponsById(item.getCouponId()).getData();
                    if(couponDto.getActivityDto().getId()==item.getActId()
                            && couponDto.getBeginTime().compareTo(LocalDateTime.now())<=0
                            && couponDto.getEndTime().compareTo(LocalDateTime.now())>=0
                            && couponDto.getState()==0)
                        orderItem.setCouponId(item.getCouponId());
                }
            }
            if (item.getQuantity() <= onsaleDto.getMaxQuantity()){
                //不能超过最大可购买数量
                orderItem.setQuantity(item.getQuantity());
                //quantity
            }else{
                throw new BusinessException(ReturnNo.ITEM_OVERMAXQUANTITY, String.format(ReturnNo.ITEM_OVERMAXQUANTITY.getMessage(), onsaleDto.getId(), item.getQuantity(), onsaleDto.getMaxQuantity()));
            }
            //2、根据商铺划分商品
            Long shopId = onsaleDto.getShop().getId();
            List<OrderItem> pack = packs.get(shopId);
            if (null == pack){
                packs.put(shopId, new ArrayList<>(){
                    {
                        add(orderItem);
                    }
                });
            }else{
                pack.add(orderItem);
            }
        });
        return packs;
    }

    @Transactional
    public void saveOrder(Map<Long, List<OrderItem>> packs, ConsigneeDto consignee, String message, UserDto customer){
        //计算订单需要支付的费用 origin_price - discount_price - point + express_fee
        //origin_price=SUM(price * quantity)
        //discount_price=SUM(discount_price * quantity)
        packs.keySet().stream().forEach(shopId -> {
            Order order = Order.builder().creatorId(customer.getId()).customerId(customer.getId()).creatorName(customer.getName()).gmtCreate(LocalDateTime.now()).shopId(shopId).
                    consignee(consignee.getConsignee()).address(consignee.getAddress()).mobile(consignee.getMobile()).regionId(consignee.getRegionId()).
                    orderSn(Common.genSeqNum(serverNum)).message(message).orderItems(packs.get(shopId)).build();
            this.discountPriceHandler.handle(packs.get(shopId),shopId,consignee.getRegionId());
            order.setDiscountPrice(this.discountPriceHandler.getFee());
            order.setOriginPrice(this.originPriceHandler.getFee());
            order.setExpressFee(this.expressFeeHandler.getFee());
            this.orderDao.createOrder(order);
                }
        );
    }


    public void createOrder(List<OrderItemDto> items, ConsigneeDto consignee, String message, UserDto customer) {
        Map<Long, List<OrderItem>> packs = this.packOrder(items, customer);
        String packStr = JacksonUtil.toJson(packs);
        //通过消息队列来创建订单起到削峰填谷的作用
        Message msg = MessageBuilder.withPayload(packStr).setHeader("consignee", consignee).setHeader("message",message).setHeader("user", customer).build();
        orderCreateRocketMQTemplate.sendMessageInTransaction("order-topic:1", msg, null);
    }

    public void updateOrderByCustomer(Long id, UpdateOrderVo orderVo, UserDto user){
        Order order=orderDao.findById(id);
        if(order.getCustomerId()!=user.getId()){
            throw new BusinessException(ReturnNo.AUTH_NO_RIGHT);
        }
        if(order.getStatus()==Order.SEND||order.getStatus()==Order.CANCEL||order.getStatus()==Order.COMPLETE||order.getStatus()==Order.REFUND){
            throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(), "订单",id,order.getStatus()));
        }
        order.setConsignee(orderVo.getConsignee());
        order.setRegionId(orderVo.getRegionId());
        order.setAddress(orderVo.getAddress());
        order.setMobile(orderVo.getMobile());
        orderDao.saveById(order,user);
    }

    @Transactional
    public PageDto<OrderSummaryDto> retrieveOrders(Long shopId, Long customerId, String orderSn, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize, UserDto user){
        List<OrderSummaryDto> pageDto=orderDao.retrieveByShopId(shopId,customerId,orderSn,beginTime,endTime,page,pageSize)
                .stream()
                .map(order -> {
                    OrderSummaryDto dto = cloneObj(order,OrderSummaryDto.class);
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageDto<>(pageDto, 0, page, pageSize, 0);
    }

    @Transactional
    public PageDto<OrderSummaryDto> retrieveCustomerOrders(Integer status,String orderSn, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize, UserDto user){
        if(user==null){
            throw new BusinessException(ReturnNo.AUTH_NEED_LOGIN,ReturnNo.AUTH_NEED_LOGIN.getMessage());
        }
        PageInfo<Order> orderPageInfo = orderDao.retrieveByOrderSnAndStatus(orderSn, status, beginTime, endTime, page, pageSize);
        List<OrderSummaryDto> pageDto=orderPageInfo.getList()
                .stream()
                .map(order -> {
                    OrderSummaryDto dto=cloneObj(order,OrderSummaryDto.class);
                    dto.setDiscountPrice(order.getDiscountPrice());
                    dto.setGmtCreate(order.getGmtCreate());
                    dto.setStatus(order.getStatus());
                    dto.setId(order.getId());
                    dto.setExpressFee(order.getExpressFee());
                    dto.setOriginPrice(order.getOriginPrice());
                    return dto;
                })
                .collect(Collectors.toList());
        return new PageDto<>(pageDto, 0, page, pageSize, 0);
    }



    @Transactional
    public void updateOrderMessage(Long shopId, Long id, String message, UserDto user){
        if(user==null){
            throw new BusinessException(ReturnNo.AUTH_NEED_LOGIN,ReturnNo.AUTH_NEED_LOGIN.getMessage());
        }
        Order order=orderDao.findByShopIdAndOrderId(shopId,id);
        order.setMessage(message);
        orderDao.saveById(order,user);
    }

    public void payOrderByCustomer(Long id, PayVo payVo, UserDto user) {
        Order order = this.orderDao.findById(id);
        if(order.getCustomerId() != user.getId()){
            System.out.println(order.getId() + " " + order.getCustomerId() + " " + user.getId());
            throw new BusinessException(ReturnNo.AUTH_NO_RIGHT);
        }
        Integer status=order.getStatus();
        if((status==Order.CANCEL || status == Order.REFUND || status == Order.COMPLETE || status == Order.SEND)){
            throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(), "订单", id, order.getStatus()));
        }
        List<MessageItemDto> itemDtos = order.getOrderItems().stream().map(orderItem -> MessageItemDto.builder().id(orderItem.getId()).name(orderItem.getName())
                .discount(orderItem.getDiscountPrice()).quantity(orderItem.getQuantity())
                .price(orderItem.getPrice()).couponId(orderItem.getCouponId()).activityId(orderItem.getActId()).build()).collect(Collectors.toList());
        MessageOrderDto messageOrderDto=MessageOrderDto.builder().id(order.getId()).orderItems(itemDtos).build();
        String orderStr = JacksonUtil.toJson(messageOrderDto);
        String userStr=JacksonUtil.toJson(user);
        String payStr = JacksonUtil.toJson(payVo);
        Message msg = MessageBuilder.withPayload(orderStr).setHeader("user", userStr).setHeader("pay", payStr).build();
        orderPayRocketMQTemplate.sendMessageInTransaction("Pay-Order", msg,null);
    }

    public void payOrder(Order order, Long id, UserDto customer){
        PayDto payDto = new PayDto();
        payDto.setTimeBegin(LocalDateTime.now());
        payDto.setTimeExpire(LocalDateTime.now());
        Long amount = order.getOriginPrice() - order.getDiscountPrice();
        if(null != order.getPoint()){
            amount -= order.getPoint();
        }
        if(null != order.getExpressFee()){
            amount += order.getExpressFee();
        }
        payDto.setAmount(amount);
        payDto.setSpOpenid(customer.getName());
        payDto.setShopChannelId(id);
        SimplePayTransDto simplePayTransDto = paymentDao.createPayment(payDto).getData();
        OrderPayment orderPayment = new OrderPayment();
        orderPayment.setOrderId(order.getId());
        orderPayment.setPaymentId(simplePayTransDto.getId());
        orderPaymentDao.save(orderPayment, customer);
    }

    public OrderDto retrieveOrder(Long shopId, Long id, UserDto user){
        Order order = orderDao.findById(id);
        OrderDto ret = cloneObj(order, OrderDto.class);
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(order.getCustomerId());
        ShopDto shopDto = new ShopDto();
        shopDto.setId(shopId);
        ConsigneeDto consigneeDto = new ConsigneeDto();
        consigneeDto.setConsignee(order.getConsignee());
        consigneeDto.setAddress(order.getAddress());
        consigneeDto.setMobile(order.getMobile());
        consigneeDto.setRegionId(order.getRegionId());
        PackDto packDto = new PackDto();
        packDto.setId(order.getPackageId());
        List<OrderItemsDto> list = order.getOrderItems()
                .stream()
                .map(orderItem -> {
                    OrderItemsDto orderItemsDto = cloneObj(orderItem, OrderItemsDto.class);
                    ActivityDto activityDto = new ActivityDto();
                    activityDto.setId(orderItem.getActId());
                    orderItemsDto.setActivity(activityDto);
                    cn.edu.xmu.oomall.order.service.dto.CouponDto couponDto = new cn.edu.xmu.oomall.order.service.dto.CouponDto();
                    couponDto.setId(orderItem.getCouponId());
                    orderItemsDto.setCoupon(couponDto);
                    return orderItemsDto;
                }).collect(Collectors.toList());
        ret.setCustomer(customerDto);
        ret.setShop(shopDto);
        ret.setConsignee(consigneeDto);
        ret.setPack(packDto);
        ret.setOrderItems(list);
        return ret;
    }

    public ReturnObject retrieveAllOrderStatus(){
        List<OrderStatusDto> data = orderDao.retrieveAllOrder().stream().map(order ->
                OrderStatusDto.builder().code(order.getStatus()).name(Order.STATUSNAMES.get(order.getStatus())).build()
        ).collect(Collectors.toList());
        return new ReturnObject(data);
    }


    @Transactional
    public void cancelOrderByCustomer(Long id,UserDto user){
        Order order=orderDao.findById(id);
        if(order.getCustomerId()!=user.getId()){
            throw new BusinessException(ReturnNo.AUTH_NO_RIGHT);
        }
        Integer status=order.getStatus();
        if(!(status==Order.BALANCE||status==Order.CLUSTER||status==Order.NOTSEND||status==Order.PAID)){
            throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(), "订单",id,order.getStatus()));
        }
        List<MessageItemDto> itemDtos = order.getOrderItems().stream().map(orderItem -> MessageItemDto.builder().id(orderItem.getId()).name(orderItem.getName())
                .discount(orderItem.getDiscountPrice()).quantity(orderItem.getQuantity())
                .price(orderItem.getPrice()).couponId(orderItem.getCouponId()).activityId(orderItem.getActId()).build()).collect(Collectors.toList());
        MessageOrderDto messageOrderDto=MessageOrderDto.builder().id(order.getId()).orderItems(itemDtos).build();
        String orderStr = JacksonUtil.toJson(messageOrderDto);
        String userStr=JacksonUtil.toJson(user);
        Message msg = MessageBuilder.withPayload(orderStr).setHeader("user", userStr).build();
        orderCancelRocketMQTemplate.sendMessageInTransaction("Revoke-Order", msg,null);

    }



    /**
     * 针对订单的一笔支付单退款
     * @param orderPayment
     * @param order
     * @param user
     */
    @Transactional
    public void refundOrder(OrderPayment orderPayment,Order order,UserDto user){
        //调用支付模块获取支付单
        FullPayTransDto fullPayTransDto=paymentDao.getPayment(order.getShopId(),orderPayment.getPaymentId()).getData();
        //调用支付模块退款
        RefundDto refundDto= new RefundDto();
        refundDto.setAmount(fullPayTransDto.getAmount());
        refundDto.setDivAmount(fullPayTransDto.getDivAmount());
        RefundTransDto refundTransDto= paymentDao.createRefund(order.getShopId(),fullPayTransDto.getId(),refundDto).getData();
        //将订单退款写入数据库
        OrderRefund orderRefund=new OrderRefund();
        orderRefund.setOrderId(order.getId());
        orderRefund.setRefundId(refundTransDto.getId());
        orderRefundDao.save(orderRefund,user);
        //将数据库中订单修改为已取消
        order.setStatus(Order.CANCEL);
        orderDao.saveById(order,user);
    }

    @Transactional
    public ReturnObject delOrders(UserDto user, Long shopId, Long id) {
        return orderDao.delById(orderDao.findById(id));
    }

    //将一个状态为待发货的订单改为待收货，并记录运单信息。
    @Transactional
    public void updateOrderStatus(UserDto user,Long shopId, Long id){
        if(user==null){
            throw new BusinessException(ReturnNo.AUTH_NEED_LOGIN,ReturnNo.AUTH_NEED_LOGIN.getMessage());
        }
        Order order=orderDao.findByShopIdAndOrderId(shopId,id);
        order.setStatus(Order.SEND);
        orderDao.saveById(order,user);
    }


}
