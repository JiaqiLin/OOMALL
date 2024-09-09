package cn.edu.xmu.oomall.freight.service.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.freight.service.openfeign.ZtoParam.*;
import cn.edu.xmu.oomall.freight.service.openfeign.ZtoParam.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "ztoExpress-service")
public interface ZtoExpressService {
    /**
     *创建运单
     */
    @PostMapping("/internal/ztoExpress/create")
    InternalReturnObject<ZtoPostExpressRetObj> postExpress(@RequestHeader String appKey, @RequestHeader String dataDigest, ZtoPostExpressParam param);
    /**
     * 根据运单号查询物流
     * */
    @GetMapping("/internal/ztoExpress/search")
    InternalReturnObject<ZtoGetExpressRetObj>  getExpressByBillCode(@RequestHeader String appKey, @RequestHeader String dataDigest, ZtoGetExpressParam param);

    /**
     *取消运单
     */
    @PutMapping("/internal/ztoExpress/cancel")
    InternalReturnObject<ZtoCancelExpressRetObj> cancelExpress(@RequestHeader String appKey, @RequestHeader String dataDigest, ZtoCancelExpressParam param);
}