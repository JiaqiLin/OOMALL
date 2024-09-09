//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.controller;

import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.order.controller.vo.OrderVo;
import cn.edu.xmu.oomall.order.controller.vo.PayVo;
import cn.edu.xmu.oomall.order.controller.vo.UpdateOrderVo;
import cn.edu.xmu.oomall.order.service.OrderService;
import cn.edu.xmu.oomall.order.service.dto.ConsigneeDto;
import cn.edu.xmu.oomall.order.service.dto.OrderItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController /*Restful的Controller对象*/
@RequestMapping(produces = "application/json;charset=UTF-8")
public class CustomerController {

    private OrderService orderService;

    @Autowired
    public CustomerController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建订单
     * @param orderVo
     * @param user
     * @return
     */
    @PostMapping("/orders")
    public ReturnObject createOrder(@RequestBody @Validated OrderVo orderVo, @LoginUser UserDto user) {
        //createOrder(List<OrderItemDto> items, ConsigneeDto consignee, String message, UserDto customer)
        orderService.createOrder(orderVo.getItems().stream()
                        .map(item -> OrderItemDto.builder()
                                .onsaleId(item.getOnsaleId())
                                .quantity(item.getQuantity())
                                .actId(item.getActId())
                                .couponId(item.getCouponId())
                                .build())
                        .collect(Collectors.toList()),
                ConsigneeDto.builder()
                        .consignee(orderVo.getConsignee())
                        .address(orderVo.getAddress())
                        .regionId(orderVo.getRegionId())
                        .mobile(orderVo.getMobile())
                        .build(),
                orderVo.getMessage(), user);
        return new ReturnObject(ReturnNo.CREATED);
    }

    @PostMapping("/orders/{id}/pay")
    public ReturnObject payOrder(@PathVariable Long id,
                                 @RequestBody(required = false) @Validated PayVo payVo,
                                 @LoginUser UserDto user){
        orderService.payOrderByCustomer(id, payVo, user);
        return new ReturnObject(ReturnNo.OK);
    }
    @PutMapping("/orders/{id}")
    public ReturnObject updateOrder(@PathVariable Long id, @RequestBody @Validated UpdateOrderVo orderVo, @LoginUser UserDto user){
        orderService.updateOrderByCustomer(id,orderVo,user);
        return new ReturnObject();
    }
    @GetMapping("/orders/states")
    public ReturnObject retrieveOrdersStatus(){
        return new ReturnObject(ReturnNo.OK,orderService.retrieveAllOrderStatus());
    }

    @GetMapping("/orders")
    public ReturnObject retrieveCustomerOrdersSummary(
                                       @RequestParam(required = false) Integer status,
                                       @RequestParam(required = false) String orderSn,
                                       @RequestParam(required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beginTime,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                                       @RequestParam(required = false,defaultValue = "1") Integer page,
                                       @RequestParam(required = false,defaultValue = "10") Integer pageSize,
                                       @LoginUser UserDto user){
        return new ReturnObject(ReturnNo.OK,orderService.retrieveCustomerOrders(status, orderSn, beginTime, endTime, page, pageSize, user));
    }

    @GetMapping("/orders/{id}")
    public ReturnObject retrieveOrder(
                                      @PathVariable Long id,
                                      @LoginUser UserDto user) {
        return new ReturnObject(orderService.retrieveOrder(null, id, user));
    }


    @DeleteMapping ("/orders/{id}")
    public ReturnObject cancelOrder(@PathVariable Long id,@LoginUser UserDto user){
        orderService.cancelOrderByCustomer(id, user);
        return new ReturnObject();
    }

}
