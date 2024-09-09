package cn.edu.xmu.oomall.freight.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * ClassName WarehouseVo
 * Description  TODO
 *
 * @author Mr_X
 * @version 1.0
 * @date 2022/12/4 11:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseVo {
    @NotNull(message = "仓库名称不可为空")
    private String name;
    @NotNull(message = "仓库地址不可为空")
    private String address;
    @NotNull(message = "地区id不可为空")
    private Long regionId;
    @NotNull(message = "联系人不可为空")
    private String senderName;
    @NotNull(message = "联系电话不可为空")
    private String senderMobile;
    private Integer priority;
}
