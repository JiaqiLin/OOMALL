package cn.edu.xmu.oomall.order.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ClassName ShopDto
 * Description  TODO
 *
 * @author Mr_X
 * @version 1.0
 * @date 2022/12/20 23:32
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Builder
public class ShopDto implements Serializable {
    private Long id;
    private String name;
    private Integer type;
}
