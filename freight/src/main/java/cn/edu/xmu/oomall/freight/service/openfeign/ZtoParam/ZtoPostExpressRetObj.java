package cn.edu.xmu.oomall.freight.service.openfeign.ZtoParam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class ZtoPostExpressRetObj {
    /**
     * 返回信息
     */
    private String message;
    /**
     * 返回code
     */
    private String statusCode;
    /**
     * 返回状态
     */
    private Boolean status;
    /**
     * 返回结果
     */
    private OrderOutput result;
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    public class OrderOutput{
        /**
         * 运单号
         */
        private String billCode;
    }
}
