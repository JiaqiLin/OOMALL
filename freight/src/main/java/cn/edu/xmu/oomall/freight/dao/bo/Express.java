package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.freight.controller.vo.ExpressVo;
import cn.edu.xmu.oomall.freight.dao.RegionDao;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

import static cn.edu.xmu.javaee.core.model.Constants.MAX_RETURN;
@ToString(callSuper = true)
@NoArgsConstructor
@Data
public class Express extends OOMallObject implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(Express.class);
    /**
     * 未发货
     */
    public static final Byte NO_SEND = 0;

    @Builder
    public Express(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, Byte status, Long id1, String billCode, Long shopLogisticsId, Long senderRegionId, Region senderRegion, String senderAddress, Long deliverRegionId, Region deliverRegion, String deliverAddress, String senderName, String senderMobile, String deliverName, String deliverMobile, Long shopId, ShopLogisticsDao shopLogisticsDao, RegionDao regionDao, ShopLogistics shopLogistics) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.status = status;
        this.id = id;
        this.billCode = billCode;
        this.shopLogisticsId = shopLogisticsId;
        this.senderRegionId = senderRegionId;
        this.senderRegion = senderRegion;
        this.senderAddress = senderAddress;
        this.deliverRegionId = deliverRegionId;
        this.deliverRegion = deliverRegion;
        this.deliverAddress = deliverAddress;
        this.senderName = senderName;
        this.senderMobile = senderMobile;
        this.deliverName = deliverName;
        this.deliverMobile = deliverMobile;
        this.shopId = shopId;
        this.shopLogisticsDao = shopLogisticsDao;
        this.regionDao = regionDao;
        this.shopLogistics = shopLogistics;
    }

    /**
     * 在途
     */
    public static final Byte SENDING = 1;
    /**
     * 签收
     */
    public static final Byte SIGN_FOR = 2;
    /**
     * 取消
     */
    public static final Byte CANCEL = 3;
    /**
     * 拒收
     */
    public static final Byte REJECT_SIGN_FOR = 4;
    /**
     * 已退回
     */
    public static final Byte BACK = 5;
    /**
     * 丢失
     */
    public static final Byte MISS = 6;
    /**
     * 回收
     */
    public static final Byte RECYCLE = 7;
    /**
     * 破损
     */
    public static final Byte BROKEN = 8;
    /**
     * 状态和名称的对应
     */
    public static final Map<Byte, String> STATUSNAMES = new HashMap(){
        {
            put(NO_SEND, "未发货");
            put(SENDING, "在途");
            put(SIGN_FOR, "签收");
            put(CANCEL, "取消");
            put(REJECT_SIGN_FOR, "拒收");
            put(BACK, "已退回");
            put(MISS, "丢失");
            put(RECYCLE, "回收");
            put(BROKEN, "破损");
        }
    };

    /**
     * 允许的状态迁移
     */
    private static final Map<Byte, Set<Byte>> toStatus = new HashMap<>(){
        {
            put(NO_SEND, new HashSet<>(){
                {
                    add(SENDING);
                    add(CANCEL);
                }
            });
            put(SENDING, new HashSet<>(){
                {
                    add(SIGN_FOR);
                    add(MISS);
                    add(REJECT_SIGN_FOR);
                }
            });
            put(REJECT_SIGN_FOR, new HashSet<>(){
                {
                    add(MISS);
                    add(BACK);
                }
            });
            put(BACK, new HashSet<>(){
                {
                    add(RECYCLE);
                    add(BROKEN);
                }
            });
        }
    };

    /**
     * 是否允许状态迁移
     * @author Ming Qiu
     * <p>
     * date: 2022-11-13 0:25
     * @param status
     * @return
     */
    public boolean allowStatus(Byte status){
        boolean ret = false;

        if (null != status && null != this.status){
            Set<Byte> allowStatusSet = toStatus.get(this.status);
            if (null != allowStatusSet) {
                ret = allowStatusSet.contains(status);
            }
        }
        return ret;
    }

    /**
     * 获得当前状态名称
     * @author Ming Qiu
     * <p>
     * date: 2022-11-13 0:43
     * @return
     */
    public String getStatusName(){
        return STATUSNAMES.get(this.status);
    }

    /**
     * 运单状态
     */
    @Setter
    @Getter
    private Byte status;
    /**
     * 运单id
     */
    @Setter
    @Getter
    private Long id;

    /**
     运单号
     */
    @Setter
    @Getter
    private String billCode;
    /**
     商户物流号
     */
    @Setter
    @Getter
    private Long shopLogisticsId;

    /**
     寄件人地区id
     */
    @Setter
    @Getter
    private Long senderRegionId;

    /**
     * 寄件人地区
     */
    @Setter
    private Region senderRegion;

    /**
     寄件人详细地址
     */
    @Setter
    @Getter
    private String senderAddress;

    /**
     收件人地区id
     */
    @Setter
    @Getter
    private Long deliverRegionId;

    /**
     * 收件人地区
     */
    @Setter
    private Region deliverRegion;

    /**
     收件人详细地址
     */
    @Setter
    @Getter
    private String deliverAddress;

    /**
     寄件人名
     */
    @Setter
    @Getter
    private String senderName;

    /**
     寄件人电话
     */
    @Setter
    @Getter
    private String senderMobile;

    /**
     收件人名
     */
    @Setter
    @Getter
    private String deliverName;

    /**
     收件人电话
     */
    @Setter
    @Getter
    private String deliverMobile;

    @Setter
    @Getter
    private Long shopId;


    @Setter
    private ShopLogisticsDao shopLogisticsDao;

    @JsonIgnore
    @ToString.Exclude
    @Setter
    private RegionDao regionDao;
    @Setter
    @Getter
    private ShopLogistics shopLogistics;

    /**
     * 创建支付交易
     */
    public Express(ExpressVo expressVo) {
        this.status = NO_SEND;
        this.shopLogisticsId = expressVo.getShopLogisticsId();
        this.senderName = expressVo.getSender().getName();
        this.senderMobile=expressVo.getSender().getMobile();
        this.senderRegionId=expressVo.getSender().getRegionId();
        this.senderAddress=expressVo.getDeliver().getAddress();
        this.deliverName = expressVo.getDeliver().getName();
        this.deliverMobile=expressVo.getDeliver().getMobile();
        this.deliverRegionId=expressVo.getDeliver().getRegionId();
        this.deliverAddress=expressVo.getDeliver().getAddress();
    }

    public ShopLogistics getShopLogistics() {
        if (null == this.shopLogistics && null != this.shopLogisticsDao) {
            this.shopLogistics = this.shopLogisticsDao.findById(this.shopLogisticsId);
        }
        return shopLogistics;
    }

    public Region getSenderRegion(){
        if (null == this.senderRegion && null != this.regionDao) {
            this.senderRegion = this.regionDao.findById(this.senderRegionId);
        }
        return senderRegion;
    }

    public Region getDeliverRegion(){
        if (null == this.deliverRegion && null != this.regionDao) {
            this.deliverRegion = this.regionDao.findById(this.deliverRegionId);
        }
        return deliverRegion;
    }

}
