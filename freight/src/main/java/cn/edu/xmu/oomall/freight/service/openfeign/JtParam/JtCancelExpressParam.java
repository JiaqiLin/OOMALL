package cn.edu.xmu.oomall.freight.service.openfeign.JtParam;

import lombok.Data;

@Data
public class JtCancelExpressParam {
    private String bizContent;
    @Data
    public class BizContent{
        /**
         * 客户编码（订单类型传2时，必填）
         */
        private String customerCode;
        /**
         * 订单类型 1（散客），2（协议客户）
         */
        private String orderType="2";
        /**
         * 客户订单号   传客户自己系统的订单号
         */
        private String txlogisticId;
        /**
         * 取消原因
         */
        private String reason="";
    }

}
