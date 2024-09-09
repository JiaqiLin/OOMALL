package cn.edu.xmu.oomall.freight.controller.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ConfirmExpressVo {
    @NotNull(message="验收状态必填")
    private Integer status;
}
