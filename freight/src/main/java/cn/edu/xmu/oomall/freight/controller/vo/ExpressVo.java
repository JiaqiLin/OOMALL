package cn.edu.xmu.oomall.freight.controller.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ExpressVo {


    /**
     * 商铺物流id
     */
    @NotNull(message="物流渠道必填")
    private Long shopLogisticsId;
    /**
     * 收件人
     */
    @NotNull(message="收件人信息必填")
    private  ConsigneeVo deliver;
    /**
     * 寄件人
     */
    private  ConsigneeVo sender;

}
