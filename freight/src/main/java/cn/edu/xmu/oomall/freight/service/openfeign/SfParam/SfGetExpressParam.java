package cn.edu.xmu.oomall.freight.service.openfeign.SfParam;

import lombok.Data;

import java.util.List;

@Data
public class SfGetExpressParam {
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
         * 查询号类别:
         * 1:根据顺丰运单号查询,trackingNumber将被当作顺丰运单号处理
         * 2:根据客户订单号查询,trackingNumber将被当作客户订单号处理
         */
        private Integer trackingType;
        /**
         * 查询号:
         * trackingType=1,则此值为顺丰运单号
         * 如果trackingType=2,则此值为客户订单号
         */
        private List<String> trackingNumber;
    }



}
