package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.dao.bo.Undeliverable;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseLogistics;
import cn.edu.xmu.oomall.freight.mapper.generator.UndeliverablePoMapper;
import cn.edu.xmu.oomall.freight.mapper.generator.WarehouseLogisticsPoMapper;
import cn.edu.xmu.oomall.freight.mapper.generator.po.UndeliverablePo;
import cn.edu.xmu.oomall.freight.mapper.generator.po.UndeliverablePoExample;
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
import static cn.edu.xmu.javaee.core.util.Common.putGmtFields;

@Repository
public class UndeliverableDao {

    private static final Logger logger = LoggerFactory.getLogger(UndeliverableDao.class);

    private UndeliverablePoMapper undeliverablePoMapper;
    private RegionDao regionDao;

    @Autowired
    public UndeliverableDao(UndeliverablePoMapper undeliverablePoMapper,RegionDao regionDao){
        this.regionDao=regionDao;
        this.undeliverablePoMapper=undeliverablePoMapper;
    }
    private Undeliverable getBo(UndeliverablePo po){
        Undeliverable ret=cloneObj(po, Undeliverable.class);
        this.setBo(ret);
        return ret;
    }

    private void setBo(Undeliverable bo){
        bo.setRegionDao(this.regionDao);
    }

    public Undeliverable findById(Long id)throws RuntimeException{
        Undeliverable ret = null;
        if(null!=id){
            UndeliverablePo po = undeliverablePoMapper.selectByPrimaryKey(id);
            if (null == po) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "不可达", id));
            }
            ret=this.getBo(po);
        }
        logger.debug("findById: id = " + id + " ret = " + ret);
        return ret;
    }

    public Undeliverable findByRegionIdAndShopLogisticsId(Long regionId,Long shopLogisticsId){
        Undeliverable ret = null;
        UndeliverablePoExample example = new UndeliverablePoExample();
        UndeliverablePoExample.Criteria criteria=example.createCriteria();
        criteria.andRegionIdEqualTo(regionId).andShopLogisticsIdEqualTo(shopLogisticsId);
        List<UndeliverablePo> poList = this.undeliverablePoMapper.selectByExample(example);
        if(poList.size()>0){
            ret=this.getBo(poList.get(0));
        }
        return ret;
    }

    public void save(Undeliverable bo, UserDto user)throws RuntimeException{
        UndeliverablePo po=cloneObj(bo,UndeliverablePo.class);
        putUserFields(po,"creator",user);
        putGmtFields(po,"create");
        undeliverablePoMapper.insertSelective(po);
        bo.setId(po.getId());

    }


    public void saveById(Undeliverable undeliverable,UserDto user)throws RuntimeException{
        if(null!=undeliverable&&null!=undeliverable.getId()){
            UndeliverablePo po=cloneObj(undeliverable,UndeliverablePo.class);
            if (null != user) {
                putUserFields(po, "modifier", user);
                putGmtFields(po, "modified");
            }
            int ret= undeliverablePoMapper.updateByPrimaryKeySelective(po);
            if(0==ret){
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"不可达物流", po.getId()));
            }
        }
    }

    public void delById(Undeliverable undeliverable)throws RuntimeException{
        if(null!=undeliverable.getId()){
            undeliverablePoMapper.deleteByPrimaryKey(undeliverable.getId());
        }
    }

    public List<Undeliverable> retrieveByShopLogisticsId(Long shopLogisticsId, Integer page, Integer pageSize)throws RuntimeException{
        List<Undeliverable> ret =new ArrayList<>();
        if(null!=shopLogisticsId){
            UndeliverablePoExample example=new UndeliverablePoExample();
            UndeliverablePoExample.Criteria criteria=example.createCriteria();
            criteria.andShopLogisticsIdEqualTo(shopLogisticsId);
            PageHelper.startPage(page,pageSize,false);
            List<UndeliverablePo> poList=this.undeliverablePoMapper.selectByExample(example);
            if(poList.size()>0){
                ret=poList.stream().map(po->this.getBo(po)).collect(Collectors.toList());
            }
        }
        return ret;
    }


}
