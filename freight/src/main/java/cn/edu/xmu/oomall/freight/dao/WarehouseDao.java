package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.mapper.generator.WarehousePoMapper;
import cn.edu.xmu.oomall.freight.mapper.generator.po.*;
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
public class WarehouseDao {
    private static final Logger logger = LoggerFactory.getLogger(WarehouseDao.class);

    private WarehousePoMapper warehousePoMapper;

    private WarehouseRegionDao warehouseRegionDao;

    private RegionDao regionDao;

    private WarehouseLogisticsDao warehouseLogisticsDao;

    @Autowired
    public WarehouseDao(WarehousePoMapper warehousePoMapper, RegionDao regionDao,WarehouseRegionDao warehouseRegionDao,WarehouseLogisticsDao warehouseLogisticsDao){
        this.warehousePoMapper = warehousePoMapper;
        this.regionDao = regionDao;
        this.warehouseRegionDao=warehouseRegionDao;
        this.warehouseLogisticsDao=warehouseLogisticsDao;
    }

    /**
     * 获取bo对象
     * @param po
     * @return
     */
    private Warehouse getBo(WarehousePo po){
        Warehouse ret;
        logger.debug("getBo po = {}", po);
        ret = cloneObj(po, Warehouse.class);
        this.setBo(ret);
        logger.debug("getBo ret = {}", ret);
        return ret;
    }

    /**
     * 设置bo对象
     * @param bo
     * @return
     */
    private void setBo(Warehouse bo) {
        bo.setWarehouseRegionDao(this.warehouseRegionDao);
        bo.setWarehouseLogisticsDao(this.warehouseLogisticsDao);
        bo.setRegionDao(this.regionDao);
    }

    /**
     * 根据主键获取仓库对象
     * @param id
     * @return
     */
    public Warehouse findById(Long id)throws RuntimeException{
        Warehouse ret = null;
        if(null != id){
            WarehousePo po = warehousePoMapper.selectByPrimaryKey(id);
            ret = this.getBo(po);
        }
        logger.debug("findById: id = " + id + " ret = " + ret);
        return ret;
    }

    /**
     * 更新仓库信息
     * @param warehouse
     * @param user
     * @return
     */
    public ReturnObject saveById(Warehouse warehouse, UserDto user)throws RuntimeException{
        if(null!=warehouse&&null!=warehouse.getId()){
            WarehousePo po=cloneObj(warehouse,WarehousePo.class);
            if (null != user) {
                putUserFields(po, "modifier", user);
                putGmtFields(po, "modified");
            }
            int ret= warehousePoMapper.updateByPrimaryKeySelective(po);
            if(0==ret){
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"仓库物流", po.getId()));
            }
        }
        return new ReturnObject(ReturnNo.OK);
    }

    /**
     * 根据主键删除仓库
     * @param warehouse
     * @return
     */
    public ReturnObject delById(Warehouse warehouse)throws RuntimeException{
        if(null == warehouse.getId()){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"仓库", warehouse.getId()));

        }
        warehousePoMapper.deleteByPrimaryKey(warehouse.getId());
        return new ReturnObject(ReturnNo.OK);
    }
    /**
     * 根据商铺id查询仓库信息
     * @param shopId
     * @param page
     * @param pageSize
     * @return
     */
    public PageInfo<Warehouse> retrieveByShopId(Long shopId, Integer page, Integer pageSize)throws RuntimeException{
        List<Warehouse> ret =new ArrayList<>();
        if(null!=shopId){
            WarehousePoExample example=new WarehousePoExample();
            WarehousePoExample.Criteria criteria=example.createCriteria();
            criteria.andShopIdEqualTo(shopId);
            PageHelper.startPage(page,pageSize,false);
            List<WarehousePo> poList=this.warehousePoMapper.selectByExample(example);
            if(poList.size()>0){
                ret=poList.stream().map(po->this.getBo(po)).collect(Collectors.toList());
            }
        }
        return new PageInfo<>(ret);
    }

    public List<Warehouse> retrieveByRegionId(Long regionId, Integer page, Integer pageSize)throws RuntimeException{
        List<Warehouse> ret =new ArrayList<>();
        if(null != regionId){
            WarehousePoExample example=new WarehousePoExample();
            WarehousePoExample.Criteria criteria=example.createCriteria();
            criteria.andRegionIdEqualTo(regionId);
            example.setOrderByClause("priority asc");
            PageHelper.startPage(page,pageSize,false);
            List<WarehousePo> poList=this.warehousePoMapper.selectByExample(example);
            if(poList.size()>0){
                ret=poList.stream().map(po->this.getBo(po)).collect(Collectors.toList());
            }
        }
        return ret;
    }

    public Warehouse findByRegionId(Long regionId){
        Warehouse ret=null;
        WarehousePoExample example=new WarehousePoExample();
        WarehousePoExample.Criteria criteria=example.createCriteria();
        criteria.andRegionIdEqualTo(regionId);
        example.setOrderByClause("priority asc");
        List<WarehousePo> poList=this.warehousePoMapper.selectByExample(example);
        if(poList.size()>0){
            ret=cloneObj(poList.get(0),Warehouse.class);
        }
        return ret;
    }

    /**
     * 查询所有有效的仓库
     * @param page
     * @param pageSize
     * @return
     */
    public PageInfo<Warehouse> retrieveValid(Integer page, Integer pageSize)throws RuntimeException{
        List<Warehouse> ret =new ArrayList<>();
        WarehousePoExample example=new WarehousePoExample();
        WarehousePoExample.Criteria criteria=example.createCriteria();
        criteria.andInvalidEqualTo(Warehouse.VALID);
        PageHelper.startPage(page,pageSize,false);
        List<WarehousePo> poList=this.warehousePoMapper.selectByExample(example);
        if(poList.size()>0){
            ret=poList.stream().map(po->this.getBo(po)).collect(Collectors.toList());
        }
        return new PageInfo<>(ret);
    }

    /**增加仓库信息
     * @param warehouse
     * @param user
     * @return
     */
    public ReturnObject save(Warehouse warehouse, UserDto user) throws RuntimeException{
        logger.debug("insertObj: warehouse = {}", warehouse);
        WarehousePo po = cloneObj(warehouse,WarehousePo.class);
        putUserFields(po, "creator", user);
        putGmtFields(po, "create");
        logger.debug("insertObj: po = {}", po);
        int ret = warehousePoMapper.insertSelective(po);
        if(ret==0){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "仓库", warehouse.getId()));
        }
        warehouse.setId(po.getId());
        return new ReturnObject(ReturnNo.OK);
    }

    public List<Warehouse> retrieveAll(){
        List<Warehouse> ret = null;
        WarehousePoExample example = new WarehousePoExample();
        List<WarehousePo> poList= warehousePoMapper.selectByExample(example);
        if(poList.size()>0){
            ret=poList.stream().map(po->getBo(po)).collect(Collectors.toList());
        }
        return ret;
    }

    public List<Warehouse> retrieveOrderByPriority(){
        List<Warehouse> ret = null;
        WarehousePoExample example = new WarehousePoExample();
        example.setOrderByClause("priority desc");
        List<WarehousePo> poList= warehousePoMapper.selectByExample(example);
        if(poList.size()>0){
            ret=poList.stream().map(po->getBo(po)).collect(Collectors.toList());
        }
        return ret;
    }
}
