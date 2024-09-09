package cn.edu.xmu.oomall.freight.service.openfeign.SfParam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class SfPostExpressRetObj {
    /**
     * true 请求成功，false 请求失败
     */
    private String success;
    /**
     * 错误编码，S0000成功
     */
    private String 	errorCode;
    /**
     * 错误描述
     */
    private String 	errorMsg;
    /**
     * 返回的详细数据
     */
    private OrderResponse msgData;
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    public static class OrderResponse{
        /**
         * 客户订单号
         */
        private String orderId;
        /**
         * 	顺丰运单号
         */
        private List<WaybillNoInfo> waybillNoInfoList;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    public static class WaybillNoInfo{
        /**
         * 运单号类型 1：母单 2 :子单 3 : 签回单
         */
         private String waybillType;
        /**
         * 运单号
         */
        private String waybillNo;
    }

}
