package cn.edu.xmu.oomall.freight.service.openfeign.ZtoParam;

import cn.edu.xmu.oomall.freight.service.openfeign.JtParam.JtCancelExpressRetObj;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
public class ZtoCancelExpressRetObj {
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
    private BusinessData result;
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public class BusinessData{
        private  String result;
    }
}
