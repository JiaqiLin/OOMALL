package cn.edu.xmu.oomall.freight.service.dto;

import lombok.Builder;
import lombok.Data;


@Data
public class ConsigneeDto {
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

    @Builder
    public ConsigneeDto(String name, String mobile, Long regionId, String address) {
        this.name = name;
        this.mobile = mobile;
        this.regionId = regionId;
        this.address = address;
    }
}
