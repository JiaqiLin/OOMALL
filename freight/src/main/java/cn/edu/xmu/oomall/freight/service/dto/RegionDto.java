package cn.edu.xmu.oomall.freight.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName RegionDto
 * Description  TODO
 *
 * @author Mr_X
 * @version 1.0
 * @date 2022/12/4 14:17
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RegionDto {
    private Long id;
    private String name;

}
