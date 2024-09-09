package cn.edu.xmu.oomall.freight.service.openfeign.ZtoParam;

import lombok.Data;

@Data
public class ZtoGetExpressParam {
    private String Body;
    @Data
    public class Body {
        /**
         * 运单号
         */
        private String billCode;
    }
}
