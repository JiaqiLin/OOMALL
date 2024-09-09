package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.oomall.freight.dao.LogisticsDao;
import cn.edu.xmu.oomall.freight.dao.RegionDao;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.UndeliverableDao;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.edu.xmu.javaee.core.model.bo.OOMallObject;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopLogistics extends OOMallObject implements Serializable {

    private static  final Logger logger = LoggerFactory.getLogger(ShopLogistics.class);
    public static Byte VALID = 0;

    public static Byte INVALID = 1;

    private Long shopId;

    private Long logisticsId;

    private Byte invalid;

    private String secret;

    private Integer priority;

    private  Logistics logistics;

    @Setter
    private LogisticsDao logisticsDao;

    @Setter
    private RegionDao regionDao;

    @Setter
    private UndeliverableDao undeliverableDao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getLogisticsId() {
        return logisticsId;
    }

    public void setLogisticsId(Long logisticsId) {
        this.logisticsId = logisticsId;
    }

    public Byte getInvalid() {
        return invalid;
    }

    public void setInvalid(Byte invalid) {
        this.invalid = invalid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret == null ? null : secret.trim();
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Logistics getLogistics() throws BusinessException {
        if (null == this.logistics && null != this.logisticsDao){
            logger.debug("getLogistics: this.shopLogisticId = {}", this.logisticsId);
            this.logistics = this.logisticsDao.findById(this.logisticsId);
        }
        return this.logistics;
    }

    /**
     * 根据判断当前商户物流是否不可达指定地区
     * @param regionId
     * @return
     */
    public boolean ifUndeliverableRegion(Long regionId){
        Undeliverable undeliverable=undeliverableDao.findByRegionIdAndShopLogisticsId(regionId,this.id);
        if(undeliverable==null){
            List<Region> parents=regionDao.retrieveParentsByRegionId(regionId).getData();
            parents=parents.stream().filter(region -> undeliverableDao.findByRegionIdAndShopLogisticsId(region.getId(),this.id)!=null).collect(Collectors.toList());
            return parents.size()!=0;
        }
        else
            return true;
    }
}
