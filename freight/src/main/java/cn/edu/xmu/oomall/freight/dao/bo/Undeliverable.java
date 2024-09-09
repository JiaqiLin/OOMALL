package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.freight.dao.RegionDao;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Undeliverable extends OOMallObject implements Serializable {
    private static  final Logger logger = LoggerFactory.getLogger(Undeliverable.class);
    /**
     * 有效
     */
    public static Integer VALID = 0;
    /**
     * 无效
     */
    public static Integer INVALID = 1;

    /*
    状态
     */
    private Byte invalid;

    /**
     * 状态和名称的对应
     */
    public static final Map<Integer, String> STATUSNAMES = new HashMap(){
        {
            put(VALID, "有效");
            put(INVALID, "无效");
        }
    };

    /**
     * 是否允许状态迁移
     */
    public boolean allowStatus(Byte status){
        return true;
    }

    /**
     * id
     */
    private Long id;

    /**
     * regionId
     */
    private Long regionId;

    /**
     * 开始时间
     */
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 商铺物流id
     */
    private Long shopLogisticsId;

    private Region region;

    @Setter
    private RegionDao regionDao;

    private ShopLogistics shopLogistics;

    @Setter
    private ShopLogisticsDao shopLogisticsDao;

    public Region getRegion(){
        if (null == this.region && null != this.regionDao){
            logger.debug("getRegion: this.regionId = {}", this.regionId);
            this.region = this.regionDao.findById(Long.valueOf(this.regionId));
        }
        return this.region;
    }

    public ShopLogistics getShopLogistics() throws BusinessException{
        if(null==this.shopLogistics&&null!=this.shopLogisticsDao){
            this.shopLogistics=this.shopLogisticsDao.findById(shopLogisticsId);
        }
        return this.shopLogistics;
    }
}
