//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.dao.openfeign.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnsaleDto {

    private Long id;//onsaleId
    private IdNameTypeDto shop;//所属商铺
    private IdNameDto product;//对应的产品
    private Long price;//价格（单位分）
    private LocalDateTime beginTime;//开始时间
    private LocalDateTime endTime;//结束时间
    private Integer quantity;//可销售的数量
    private Integer maxQuantity;//单次可买最大数量
    private Byte type;//0为普通，1为秒杀，2为团购，3为预售
    private List<IdNameTypeDto> actList;//参与活动
}
