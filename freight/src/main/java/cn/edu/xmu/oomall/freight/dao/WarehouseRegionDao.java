package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.dao.bo.Undeliverable;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseRegion;
import cn.edu.xmu.oomall.freight.mapper.generator.WarehouseRegionPoMapper;
import cn.edu.xmu.oomall.freight.mapper.generator.po.ExpressPo;
import cn.edu.xmu.oomall.freight.mapper.generator.po.WarehouseLogisticsPo;
import cn.edu.xmu.oomall.freight.mapper.generator.po.WarehouseRegionPo;
import cn.edu.xmu.oomall.freight.mapper.generator.po.WarehouseRegionPoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import cn.edu.xmu.javaee.core.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class WarehouseRegionDao{

    private static  final Logger logger = LoggerFactory.getLogger(WarehouseRegion.class);

    private WarehouseRegionPoMapper warehouseRegionPoMapper;

    private RegionDao regionDao;

    @Autowired
    public WarehouseRegionDao(RegionDao regionDao,WarehouseRegionPoMapper warehouseRegionPoMapper){
        this.regionDao=regionDao;
        this.warehouseRegionPoMapper=warehouseRegionPoMapper;
    }

    /**
     * 获得bo对象
     * @param po
     * @return
     */
    private WarehouseRegion getBo(WarehouseRegionPo po){
        WarehouseRegion ret;
        logger.debug("getBo po={}",po);
        ret=cloneObj(po,WarehouseRegion.class);
        ret.setRegionDao(this.regionDao);
        logger.debug("getBo ret={}",ret);
        return ret;
    }

    /**
     * 按照主键获得对象
     * @param id
     * @return
     * @throws RuntimeException
     * @throws BusinessException 无此对象
     * @return
     */
    public WarehouseRegion findById(Long id) throws RuntimeException{
        WarehouseRegion ret = null;
        if (null != id) {
            WarehouseRegionPo po = warehouseRegionPoMapper.selectByPrimaryKey(id);
            if (null == po) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "仓库地区", id));
            }
            ret = getBo(po);
        }
        return ret;
    }

    /**
     * 商户新增仓库配送地区
     * @param obj 新增的仓库配送地区
     * @param user 登录用户
     * @return
     */
    public void save(WarehouseRegion obj, UserDto user) throws RuntimeException{
        logger.debug("insertObj: obj = {}", obj);
        WarehouseRegionPo po = cloneObj(obj, WarehouseRegionPo.class);
        putUserFields(po, "creator", user);
        putGmtFields(po, "create");
        logger.debug("insertObj: po = {}", po);
        warehouseRegionPoMapper.insertSelective(po);
        obj.setId(po.getId());
        obj.setRegionDao(this.regionDao);
    }

    public void saveById(WarehouseRegion obj, UserDto user)throws RuntimeException{
        if(null != obj && null != obj.getId()){
            WarehouseRegionPo po=cloneObj(obj,WarehouseRegionPo.class);
            if (null != user) {
                putUserFields(po, "modifier", user);
                putGmtFields(po, "modified");
            }
            int ret= warehouseRegionPoMapper.updateByPrimaryKeySelective(po);
            if(0 == ret){
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"仓库地区", po.getId()));
            }
        }
    }

    /**
     * 商户或管理员取消仓库对某个地区的配送
     * @param warehouseRegion
     * @return
     */
    public ReturnObject delById(WarehouseRegion warehouseRegion) throws RuntimeException{
        if(null == warehouseRegion.getId()){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"地区不存在",warehouseRegion.getId()));
        }
        warehouseRegionPoMapper.deleteByPrimaryKey(warehouseRegion.getId());
        return new ReturnObject(ReturnNo.OK);
    }

    /**
     * 商户或管理员查询某个仓库的配送地区
     * @param warehouseId
     * @param page
     * @param pageSize
     * @throws RuntimeException
     * @return
     */
    public List<WarehouseRegion> retrieveByWarehouseId(Long warehouseId,Integer page,Integer pageSize) throws RuntimeException{
        List<WarehouseRegion> ret = null;
        if(null != warehouseId){
            WarehouseRegionPoExample example=new WarehouseRegionPoExample();
            WarehouseRegionPoExample.Criteria criteria=example.createCriteria();
            criteria.andWarehouseIdEqualTo(warehouseId);
            PageHelper.startPage(page,pageSize,false);
            List<WarehouseRegionPo> poList=this.warehouseRegionPoMapper.selectByExample(example);
            if(poList.size()>0){
                ret=poList.stream().map(po->this.getBo(po)).collect(Collectors.toList());
            }else{
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "仓库地区" , warehouseId));
            }
        }
        return ret;
    }

    public List<WarehouseRegion> findSame(Long regionId,Long warehouseId){
        List<WarehouseRegion> ret=null;
        WarehouseRegionPoExample example=new WarehouseRegionPoExample();
        WarehouseRegionPoExample.Criteria criteria=example.createCriteria();
        criteria.andRegionIdEqualTo(regionId).andWarehouseIdEqualTo(warehouseId);
        List<WarehouseRegionPo> poList=this.warehouseRegionPoMapper.selectByExample(example);
        if(poList.size()>0){
            ret=poList.stream().map(po->this.getBo(po)).collect(Collectors.toList());
        }
        return ret;
    }

    public WarehouseRegion findByRegionIdAndWarehouseId(Long regionId,Long warehouseId){
        WarehouseRegion ret = null;
        WarehouseRegionPoExample example = new WarehouseRegionPoExample();
        WarehouseRegionPoExample.Criteria criteria= example.createCriteria();
        criteria.andRegionIdEqualTo(regionId).andWarehouseIdEqualTo(warehouseId);
        List<WarehouseRegionPo> poList = this.warehouseRegionPoMapper.selectByExample(example);
        if(poList.size()>0){
            ret=cloneObj(poList.get(0) , WarehouseRegion.class);
        }
        return ret;
    }
}
