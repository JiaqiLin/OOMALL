package cn.edu.xmu.oomall.order.service.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName ConsigneesDto
 * Description  TODO
 *
 * @author Mr_X
 * @version 1.0
 * @date 2022/12/20 23:35
 */
@Data
@Builder
public class ConsigneesDto implements Serializable{
    private String name;

    private String address;

    private Long regionId;

    private String mobile;
}
