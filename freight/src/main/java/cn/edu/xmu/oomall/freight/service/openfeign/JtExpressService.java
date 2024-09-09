package cn.edu.xmu.oomall.freight.service.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.freight.service.openfeign.JtParam.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "jtExpress-service")
public interface JtExpressService {
    /**
     *创建运单
     */
    @PostMapping("/internal/jtExpress/create")
    InternalReturnObject<JtPostExpressRetObj> postExpress(@RequestHeader String apiAccount,@RequestHeader String digest,@RequestHeader Long timestamp, JtPostExpressParam param);
    /**
     * 根据运单号查询物流
     * */
    @GetMapping("/internal/jtExpress/search")
    InternalReturnObject<JtGetExpressRetObj> getExpressByBillCode(@RequestHeader String apiAccount,@RequestHeader String digest,@RequestHeader Long timestamp,JtGetExpressParam param);

    /**
     *取消运单
     */
    @PutMapping("/internal/jtExpress/cancel")
    InternalReturnObject<JtCancelExpressRetObj> cancelExpress(@RequestHeader String apiAccount,@RequestHeader String digest,@RequestHeader Long timestamp,JtCancelExpressParam param);
}
