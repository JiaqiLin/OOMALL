package cn.edu.xmu.oomall.freight.service.openfeign.SfParam;

import lombok.Data;

import java.util.List;
@Data
public class SfCancelExpressParam {
    /**
     * 合作伙伴编码（即顾客编码）
     */
    private String partnerID;
    /**
     * 请求唯一号UUID
     */
    private String requestID;
    /**
     * 接口服务代码 EXP_RECE_SEARCH_ROUTES
     */
    private String serviceCode;
    /**
     * 调用接口时间戳
     */
    private long timestamp;
    /**
     * 业务数据报文
     */
    private MsgData msgData;

    @Data
    public class MsgData{
        /**
         * 客户订单号
         */
        private String orderId;
        /**
         * 顺丰运单号
         */
        private List<WaybillNoInfo> waybillNoInfoList;
    }
    @Data
    public class WaybillNoInfo{
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
