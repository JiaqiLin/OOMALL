package cn.edu.xmu.oomall.order.dao.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.FreightDto;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.ItemDto;
import cn.edu.xmu.oomall.order.service.dto.OrderItemDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name="shop-service")
public interface ShopDao {

    @PostMapping("/internal/templates/{id}/regions/{rid}/freightprice")
    InternalReturnObject<FreightDto> getFreight(@PathVariable Long id, @PathVariable Long rid, @RequestBody List<ItemDto> items);


}
