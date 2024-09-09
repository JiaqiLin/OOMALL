package cn.edu.xmu.oomall.freight.controller.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class RegionVo {
    @NotNull(message="地区名称不为空")
    private String name;

    @NotNull(message="地区简称不为空")
    private String shortName;

    @NotNull(message="地区全称不为空")
    private String mergerName;

    @NotNull(message="地区拼音不为空")
    private String pinyin;

    @NotNull(message="地区经度不为空")
    private String lng;

    @NotNull(message="地区维度不为空")
    private String lat;

    @NotNull(message="地区码不为空")
    private String areaCode;

    @NotNull(message="邮政编码不为空")
    private String zipCode;

    @NotNull(message="电话区号不为空")
    private String cityCode;

}
