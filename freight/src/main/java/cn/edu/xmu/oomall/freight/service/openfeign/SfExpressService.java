package cn.edu.xmu.oomall.freight.service.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.freight.service.openfeign.SfParam.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author Mr_X
 */
@FeignClient(name = "sfExpress-service")
public interface SfExpressService {

    /**
     * 创建运单
     */
    @PostMapping("/internal/sfExpress/create")
    InternalReturnObject<SfPostExpressRetObj> postExpress(@RequestHeader String accessToken, @RequestHeader String msgDigest,
                                                           SfPostExpressParam param);

    /**
     * 根据运单号查询物流
     */
    @GetMapping("/internal/sfExpress/search")
    InternalReturnObject<SfGetExpressRetObj> getExpressByBillCode(@RequestHeader String accessToken, @RequestHeader String msgDigest,
                                                                  SfGetExpressParam param);

    /**
     * 取消运单
     */
    @PutMapping("/internal/sfExpress/cancel")
    InternalReturnObject<SfCancelExpressRetObj> cancelExpress(@RequestHeader String accessToken, @RequestHeader String msgDigest,
                                                              SfCancelExpressParam param);
}
