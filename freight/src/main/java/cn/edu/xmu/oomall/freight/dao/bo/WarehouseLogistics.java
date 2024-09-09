package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.WarehouseDao;
import cn.edu.xmu.oomall.freight.mapper.generator.ShopLogisticsPoMapper;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseLogistics extends OOMallObject implements Serializable {

    private static  final Logger logger = LoggerFactory.getLogger(WarehouseLogistics.class);
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private Long shopId;

    @Getter
    @Setter
    private Long warehouseId;


    private ShopLogistics shopLogistics;
    @Getter
    @Setter
    private Long shopLogisticsId;

    @Getter
    @Setter
    private LocalDateTime beginTime;

    @Getter
    @Setter
    private LocalDateTime endTime;

    @Getter
    @Setter
    private Byte invalid;

    @Setter
    private ShopLogisticsDao shopLogisticsDao;

    @Setter
    private WarehouseDao warehouseDao;

    private Warehouse warehouse;

    /**
     * 有效
     */
    public static Byte VALID = 0;
    /**
     * 暂停
     */
    public static Byte INVALID = 1;

    /**
     * 状态和名称的对应
     */
    public static final Map<Byte, String> STATUSNAMES = new HashMap(){
        {
            put(VALID, "有效");
            put(INVALID, "暂停");
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
        return true;
    }

    public ShopLogistics getShopLogistics() throws BusinessException {
        if (null == this.shopLogistics && null != this.shopLogisticsDao){
            logger.debug("getShopLogistics: this.shopLogisticId = {}", this.shopLogisticsId);
            this.shopLogistics = this.shopLogisticsDao.findById(this.shopLogisticsId);
        }
        return this.shopLogistics;
    }

    public Warehouse getWarehouse() throws BusinessException {
        if(null!=this.warehouseId&&null!=this.warehouseDao){
            this.warehouse=this.warehouseDao.findById(warehouseId);
        }
        return this.warehouse;
    }
}
