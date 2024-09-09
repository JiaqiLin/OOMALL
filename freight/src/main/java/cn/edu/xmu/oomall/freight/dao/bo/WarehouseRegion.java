package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.freight.dao.RegionDao;
import cn.edu.xmu.oomall.freight.dao.WarehouseDao;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 仓库配送地区
 */
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarehouseRegion extends OOMallObject implements Serializable {

    /**
     * 商铺id
     */
    @Getter
    @Setter
    private Long shopId;

    /**
     * 仓库id
     */
    @Getter
    @Setter
    private Long warehouseId;

    /**
     * 地区id
     */
    @Getter
    @Setter
    private Long regionId;

    /**
     * 开始时间
     */
    @Setter
    @Getter
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    @Setter
    @Getter
    private LocalDateTime endTime;

    private Region region;

    @Setter
    private RegionDao regionDao;

    @Setter
    private WarehouseDao warehouseDao;

    private Warehouse warehouse;

    public WarehouseRegion(Long warehouseId, Long regionId, LocalDateTime beginTime, LocalDateTime endTime)
    {
        this.warehouseId=warehouseId;
        this.regionId=regionId;
        this.beginTime=beginTime;
        this.endTime=endTime;
    }

    public Warehouse getWarehouse() throws BusinessException {
        if(null!=this.warehouseId&&null!=this.warehouseDao){
            this.warehouse=this.warehouseDao.findById(warehouseId);
        }
        return this.warehouse;
    }

    public Region getRegion() throws BusinessException{
        if(null!=this.regionId&&null!=this.regionDao){
            this.region=this.regionDao.findById(regionId);
        }
        return this.region;
    }
}
