package cn.edu.xmu.oomall.order.dao.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "payment-service")
public interface PaymentDao {
    @GetMapping("/shops/{shopId}/payments/{id}")
    InternalReturnObject<FullPayTransDto> getPayment(@PathVariable Long shopId, @PathVariable Long id);

    @PostMapping("/shops/{shopId}/payments/{id}/refunds")
    InternalReturnObject<RefundTransDto> createRefund(@PathVariable Long shopId, @PathVariable Long id, @Validated @RequestBody(required = true) RefundDto refundDto);

    @GetMapping("/shops/{shopId}/shopchannels/{id}")
    ShopChannelDto getShopChannel(@PathVariable Long shopId, @PathVariable Long id);

    @PostMapping("/payments")
    InternalReturnObject<SimplePayTransDto> createPayment(@Validated @RequestBody(required = true) PayDto payDto);

    @GetMapping("/shops/{shopId}/shopchannels")
    List<SimpleShopChannelDto> retrieveShopChannels(@PathVariable Long shopId);
}
