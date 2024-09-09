package cn.edu.xmu.oomall.freight.service.openfeign.ZtoParam;

import lombok.Data;

@Data
public class ZtoCancelExpressParam {
    private String Body;
    @Data
    public class Body {
        /**
         * 取消类型 1不想寄了,2下错单,3重复下单,4运费太贵,5无人联系,6取件太慢,7态度差
         */
        private String cancelType = "1";
        /**
         * 订单编号
         */
        private String orderCode;
        /**
         * 运单编号
         */
        private String billCode;
    }
}
