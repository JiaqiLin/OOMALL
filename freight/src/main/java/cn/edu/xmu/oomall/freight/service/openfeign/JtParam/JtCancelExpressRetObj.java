package cn.edu.xmu.oomall.freight.service.openfeign.JtParam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class JtCancelExpressRetObj {
    /**
     * 返回码
     */
    private String code;
    /**
     * 描述
     */
    private String msg;
    /**
     * 业务数据
     */
    private BusinessData data;
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public class BusinessData{
        /**
         * 运单号
         */
        private String billCode;
        /**
         * 客户订单号
         */
        private String txlogisticId;

    }

}
