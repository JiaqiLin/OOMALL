package cn.edu.xmu.oomall.freight.controller.vo;

import lombok.Data;

@Data
public class ConsigneeVo {
    /**
     * 姓名
     */
    private String name;
    /**
     * 电话
     */
    private String mobile;
    /**
     * 地区id
     */
    private Long regionId;
    /**
     * 详细地址
     */
    private String address;
}
