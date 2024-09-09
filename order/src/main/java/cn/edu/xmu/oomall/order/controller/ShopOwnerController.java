package cn.edu.xmu.oomall.order.controller;

import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.order.controller.vo.OrderUpdateVo;
import cn.edu.xmu.oomall.order.controller.vo.OrderVo;
import cn.edu.xmu.oomall.order.service.OrderService;
import cn.edu.xmu.oomall.order.service.dto.OrderSummaryDto;
import cn.edu.xmu.oomall.order.service.dto.SimpleOrderItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(produces = "application/json;charset=UTF-8")
public class ShopOwnerController {

    private OrderService orderService;

    @Autowired
    public ShopOwnerController(OrderService orderService){
        this.orderService=orderService;
    }

    /**
     * 店家查询商户所有订单 (概要)
     * @param shopId
     * @param customerId
     * @param orderSn
     * @param beginTime gmt_Create
     * @param endTime
     * @param page
     * @param pageSize
     * @param user
     * @return
     */
    @GetMapping("/shops/{shopId}/orders")
    public ReturnObject retrieveOrders(@PathVariable Long shopId,
                                       @RequestParam(required = false) Long customerId,
                                       @RequestParam(required = false) String orderSn,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beginTime,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                                       @RequestParam(required = false,defaultValue = "1") Integer page,
                                       @RequestParam(required = false,defaultValue = "10") Integer pageSize,
                                       @LoginUser UserDto user){
        PageDto<OrderSummaryDto> dtos = orderService.retrieveOrders(shopId, customerId, orderSn, beginTime, endTime, page, pageSize,user);
        return new ReturnObject(ReturnNo.OK, dtos);
    }

    /**
     * 店家修改订单 (留言)。
     * @param shopId
     * @param id 指定订单号
     * @param orderUpdateVo
     * @param user
     * @return
     */
    @PutMapping("/shops/{shopId}/orders/{id}")
    public ReturnObject updateOrderMessage(@PathVariable Long shopId,
                                           @PathVariable Long id,
                                           @Validated @RequestBody OrderUpdateVo orderUpdateVo,
                                           @LoginUser UserDto user) {
        orderService.updateOrderMessage(shopId, id, orderUpdateVo.getMessage(), user);
        return new ReturnObject(ReturnNo.OK);
    }

    @GetMapping("/shops/{shopId}/orders/{id}")
    public ReturnObject retrieveOrder(@PathVariable Long shopId,
                                      @PathVariable Long id,
                                      @LoginUser UserDto user) {
        return new ReturnObject(orderService.retrieveOrder(shopId, id, user));
    }



    @DeleteMapping("/shops/{shopId}/orders/{id}")
    public ReturnObject delOrders( @LoginUser UserDto userDto,
                                   @PathVariable("shopId") Long shopId,
                                   @PathVariable("id") Long id){
        ReturnObject returnObject = orderService.delOrders(userDto,shopId, id);
        return returnObject;
    }

    @PutMapping("/shops/{shopId}/orders/{id}/confirm")
    public ReturnObject updateOrderStatus(@LoginUser UserDto user,
                                          @PathVariable Long shopId,
                                           @PathVariable Long id) {
        orderService.updateOrderStatus(user,shopId, id);
        return new ReturnObject(ReturnNo.OK);
    }
}
