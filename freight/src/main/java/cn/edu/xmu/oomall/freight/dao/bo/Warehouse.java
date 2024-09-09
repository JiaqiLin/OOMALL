package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.freight.dao.RegionDao;
import cn.edu.xmu.oomall.freight.dao.WarehouseLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.WarehouseRegionDao;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Warehouse extends OOMallObject implements Serializable {
    /**
     * 有效
     */
    public static Byte VALID = 0;
    /**
     * 无效
     */
    public static Byte INVALID = 1;

    /**
     * 状态和名称的对应
     */
    public static final Map<Byte, String> STATUSNAMES = new HashMap(){
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
     * 仓库id
     */
    @Getter
    @Setter
    private Long id;

    /**
     * 仓库名称
     */
    @Getter
    @Setter
    private String name;

    /**
     * 仓库详细地址
     */
    @Getter
    @Setter
    private String address;

    @Getter
    @Setter
    private LocalDateTime beginTime;

    /**
     * 仓库所在地区id
     */
    @Getter
    @Setter
    private Long regionId;

    /**
     * 联系人
     */
    @Getter
    @Setter
    private String senderName;

    /**
     * 联系电话
     */
    @Getter
    @Setter
    private String senderMobile;

    /**
     * 商铺id
     */
    @Getter
    @Setter
    private Long shopId;

    /**
     * 优先级
     */
    @Getter
    @Setter
    private Integer priority;

    /**
     * 状态
     */
    @Getter
    @Setter
    private Byte invalid;

    @Setter
    private WarehouseRegionDao warehouseRegionDao;

    @Setter
    private List<WarehouseLogistics> warehouseLogisticsList;

    @Setter
    private WarehouseLogisticsDao warehouseLogisticsDao;

    @Setter
    private RegionDao regionDao;

    private Region region;

    public List<WarehouseLogistics> getWarehouseLogisticsList() throws BusinessException {
        if (null == this.warehouseLogisticsList && null != this.warehouseLogisticsDao){
            this.warehouseLogisticsList = this.warehouseLogisticsDao.retrieveByWarehouseId(this.id,1,10).getList();
        }
        return this.warehouseLogisticsList;
    }
    public boolean ifContainRegion(Long regionId){
        WarehouseRegion warehouseRegion=warehouseRegionDao.findByRegionIdAndWarehouseId(regionId,this.id);
        if(warehouseRegion==null){
            List<Region> parents=regionDao.retrieveParentsByRegionId(regionId).getData();
            parents=parents.stream().filter(region -> warehouseRegionDao.findByRegionIdAndWarehouseId(region.getId(),this.id)!=null).collect(Collectors.toList());
            return parents.size()!=0;
        }
        else
            return true;
    }

    public void filterWarehouseLogistics(List<ShopLogistics> validShopLogisticsList){
        this.warehouseLogisticsList=getWarehouseLogisticsList().stream().filter(warehouseLogistics -> validShopLogisticsList
                                                                                                        .stream()
                                                                                                        .filter(shopLogistics -> shopLogistics.getId().equals(warehouseLogistics.getShopLogisticsId()))
                                                                                                        .count()>0 )
                                .collect(Collectors.toList());
    }

    public Region getRegion() throws BusinessException{
        if(null!=this.regionId&&null!=this.regionDao){
            this.region=this.regionDao.findById(regionId);
        }
        return this.region;
    }
}
