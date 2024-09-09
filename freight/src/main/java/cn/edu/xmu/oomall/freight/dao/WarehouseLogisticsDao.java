package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseLogistics;
import cn.edu.xmu.oomall.freight.mapper.generator.WarehouseLogisticsPoMapper;
import cn.edu.xmu.oomall.freight.mapper.generator.po.ShopLogisticsPoExample;
import cn.edu.xmu.oomall.freight.mapper.generator.po.WarehouseLogisticsPo;
import cn.edu.xmu.oomall.freight.mapper.generator.po.WarehouseLogisticsPoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class WarehouseLogisticsDao {
    private static final Logger logger = LoggerFactory.getLogger(WarehouseLogisticsDao.class);

    private WarehouseLogisticsPoMapper warehouseLogisticsPoMapper;
    private ShopLogisticsDao shopLogisticsDao;

    @Autowired
    public WarehouseLogisticsDao(WarehouseLogisticsPoMapper warehouseLogisticsPoMapper,ShopLogisticsDao shopLogisticsDao){
        this.warehouseLogisticsPoMapper=warehouseLogisticsPoMapper;
        this.shopLogisticsDao=shopLogisticsDao;
    }

    /**
     * 获取bo对象
     * @param po
     * @return
     */
    private WarehouseLogistics getBo(WarehouseLogisticsPo po){
        WarehouseLogistics ret=cloneObj(po, WarehouseLogistics.class);
        this.setBo(ret);
        return ret;
    }

    private void setBo(WarehouseLogistics bo){
        bo.setShopLogisticsDao(this.shopLogisticsDao);
    }

    public WarehouseLogistics findById(Long id)throws RuntimeException{
        WarehouseLogistics ret = null;
        if(null!=id){
            WarehouseLogisticsPo po = warehouseLogisticsPoMapper.selectByPrimaryKey(id);
            if(null==po){
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "仓库物流", id));
            }
            ret=this.getBo(po);
        }
        logger.debug("findById: id = " + id + " ret = " + ret);
        return ret;
    }

    public ReturnObject save(WarehouseLogistics bo, UserDto user)throws RuntimeException{
        WarehouseLogisticsPo po=cloneObj(bo,WarehouseLogisticsPo.class);
        putUserFields(po,"creator",user);
        putGmtFields(po,"create");
        int ret = warehouseLogisticsPoMapper.insertSelective(po);
        if(ret==0){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "仓库物流", po.getId()));
        }
        bo.setId(po.getId());
        return new ReturnObject(ReturnNo.OK);
    }


    public void saveById(WarehouseLogistics warehouseLogistics,UserDto user)throws RuntimeException{
        if(null!=warehouseLogistics&&null!=warehouseLogistics.getId()){
            WarehouseLogisticsPo po=cloneObj(warehouseLogistics,WarehouseLogisticsPo.class);
            if (null != user) {
                putUserFields(po, "modifier", user);
                putGmtFields(po, "modified");
            }
           int ret= warehouseLogisticsPoMapper.updateByPrimaryKeySelective(po);
            if(0==ret){
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"仓库物流", po.getId()));
            }
        }
    }

    public ReturnObject delById(WarehouseLogistics warehouseLogistics)throws RuntimeException{
        if(null!=warehouseLogistics.getId()){
            warehouseLogisticsPoMapper.deleteByPrimaryKey(warehouseLogistics.getId());
            return new ReturnObject(ReturnNo.OK);
        }
        throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"仓库物流", warehouseLogistics.getId()));
    }

    public PageInfo<WarehouseLogistics> retrieveByWarehouseId(Long warehouseId,Integer page, Integer pageSize)throws RuntimeException{
        List<WarehouseLogistics> ret =new ArrayList<>();
        if(null!=warehouseId){
            WarehouseLogisticsPoExample example=new WarehouseLogisticsPoExample();
            WarehouseLogisticsPoExample.Criteria criteria=example.createCriteria();
            criteria.andWarehouseIdEqualTo(warehouseId);
            PageHelper.startPage(page,pageSize,false);
            List<WarehouseLogisticsPo> poList=this.warehouseLogisticsPoMapper.selectByExample(example);
            if(poList.size()>0){
                ret=poList.stream().map(po->this.getBo(po)).collect(Collectors.toList());
            }
        }
        return new PageInfo<>(ret);
    }

    public WarehouseLogistics findByLidAndWid(Long wId,Long lId){
        if(wId == null || lId == null){
            return null;
        }
        WarehouseLogisticsPoExample example=new WarehouseLogisticsPoExample();
        WarehouseLogisticsPoExample.Criteria criteria=example.createCriteria();
        criteria.andWarehouseIdEqualTo(wId).andShopLogisticsIdEqualTo(lId);
        List<WarehouseLogisticsPo> warehouseLogisticsPos = warehouseLogisticsPoMapper.selectByExample(example);
        if(warehouseLogisticsPos == null){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"仓库物流", warehouseLogisticsPos));
        }
        return getBo(warehouseLogisticsPos.get(0));
    }




}
