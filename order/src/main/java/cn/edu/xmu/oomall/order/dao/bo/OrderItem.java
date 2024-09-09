//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.OnsaleDto;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString(callSuper = true)
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OrderItem extends OOMallObject implements Serializable {

    @Builder
    public OrderItem(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, Long orderId, Long onsaleId, Integer quantity, Long price, Long discountPrice, Long point, String name, Long actId, Long couponId, Byte commented) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.orderId = orderId;
        this.onsaleId = onsaleId;
        this.quantity = quantity;
        this.price = price;
        this.discountPrice = discountPrice;
        this.point = point;
        this.name = name;
        this.actId = actId;
        this.couponId = couponId;
        this.commented = commented;
    }


    private Long orderId;


    private Long onsaleId;

    private Long productId;

    private Long weight;//货品质量

    private Integer productQuantity;//当前可售数量

    private int quantity;//数量

    private Long price;//原价

    private Long discountPrice;//折扣单价

    private Long point;//分摊积点 - 1/10积点

    private String name;//商品名称

    private Long actId;//活动id

    private Long couponId;//优惠券id

    private Byte commented;

    private Long freightTemplateId;
}
