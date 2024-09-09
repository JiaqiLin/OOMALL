package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseLogistics;
import cn.edu.xmu.oomall.freight.mapper.generator.ExpressPoMapper;
import cn.edu.xmu.oomall.freight.mapper.generator.po.ExpressPo;
import cn.edu.xmu.oomall.freight.mapper.generator.po.ExpressPoExample;
import cn.edu.xmu.oomall.freight.mapper.generator.po.WarehouseLogisticsPo;
import cn.edu.xmu.oomall.freight.mapper.generator.po.WarehouseLogisticsPoExample;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class ExpressDao {
    private static final Logger logger = LoggerFactory.getLogger(ExpressDao.class);

    private ShopLogisticsDao shopLogisticsDao;
    private ExpressPoMapper expressPoMapper;

    private RegionDao regionDao;

    @Autowired
    public ExpressDao(ExpressPoMapper expressPoMapper,ShopLogisticsDao shopLogisticsDao,RegionDao regionDao){
        this.expressPoMapper=expressPoMapper;
        this.shopLogisticsDao=shopLogisticsDao;
        this.regionDao=regionDao;
    }

    private Express getBo(ExpressPo po) {
        Express ret;
        ret = cloneObj(po, Express.class);
        ret.setShopLogisticsDao(this.shopLogisticsDao);
        ret.setRegionDao(this.regionDao);
        return ret;
    }

    public void save(Express obj, UserDto user) throws RuntimeException{
        logger.debug("insertObj: obj = {}", obj);
        ExpressPo po = cloneObj(obj, ExpressPo.class);
        putUserFields(po, "creator", user);
        putGmtFields(po, "create");
        logger.debug("insertObj: po = {}", po);
        expressPoMapper.insertSelective(po);
        obj.setId(po.getId());
        obj.setShopLogisticsDao(this.shopLogisticsDao);
    }

    /**
     * 由id返回运单对象
     */
    public Express findById(Long id) throws RuntimeException{
        Express ret = null;
        if (null != id) {
            ExpressPo po = expressPoMapper.selectByPrimaryKey(id);
            if (null == po) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "运单", id));
            }
            ret = getBo(po);
        }
        return ret;
    }

    public void saveById(Express obj, UserDto user)throws RuntimeException{
        if(null!=obj&&null!=obj.getId()){
            ExpressPo po=cloneObj(obj,ExpressPo.class);
            if (null != user) {
                putUserFields(po, "modifier", user);
                putGmtFields(po, "modified");
            }
            int ret= expressPoMapper.updateByPrimaryKeySelective(po);
            if(0==ret){
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"运单", po.getId()));
            }
        }
    }

    public Express findByBillCode(String billCode){
        Express ret=null;
        if(null!=billCode){
            ExpressPoExample example=new ExpressPoExample();
            ExpressPoExample.Criteria criteria=example.createCriteria();
            criteria.andBillCodeEqualTo(billCode);
            List<ExpressPo> poList=this.expressPoMapper.selectByExample(example);
            if(poList.size()>0){
                ret=poList.stream().map(po->this.getBo(po)).collect(Collectors.toList()).get(0);
            }
            else {
                throw new BusinessException(ReturnNo.FREIGHT_BILLCODE_NOTEXIST);
            }
        }
        return ret;
    }
}
