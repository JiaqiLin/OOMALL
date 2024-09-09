package cn.edu.xmu.oomall.freight.service.courier.dto;

import lombok.Data;

@Data
public class CancelExpressAdaptorDto {
    /**
     * 取消状态：成功/失败
     */
    private Boolean status;
}
