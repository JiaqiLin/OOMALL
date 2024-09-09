package cn.edu.xmu.oomall.freight.service.openfeign.JtParam;

import lombok.Data;

@Data
public class JtGetExpressParam {
    private String bizContent;
    @Data
    public class BizContent{
        /**
         * 运单号，多个运单号以英文逗号隔开,目前先支持一次性最多查询30票运单
         */
        private String billCodes;
    }
}
