package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.freight.dao.LogisticsDao;
import cn.edu.xmu.oomall.freight.service.LogisticsService;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Logistics extends OOMallObject implements Serializable {

    private static  final Logger logger = LoggerFactory.getLogger(Logistics.class);



    /*
    * 物流渠道名称
    * */


    private String name;

    /*
    * 平台的应用id
    * */

    private String appId;

    /*
    * 密钥
    * */

    private String secret;

    /*
    * 运单编号匹配规则
    * */

    private String snPattern;

    /**
     * 适配对象名
     */

    private String logisticsClass;

    public boolean isMatch(String billCode){
        return Pattern.matches(this.snPattern,billCode);
    }



}
