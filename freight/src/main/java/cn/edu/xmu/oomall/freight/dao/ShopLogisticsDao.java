package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.mapper.generator.RegionPoMapper;
import cn.edu.xmu.oomall.freight.mapper.generator.ShopLogisticsPoMapper;
import cn.edu.xmu.oomall.freight.mapper.generator.po.*;
import cn.edu.xmu.oomall.freight.mapper.generator.po.ShopLogisticsPo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.plaf.synth.Region;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

/**
 * ClassName ShopLogisticsDao
 * Description  TODO
 *
 * @author Wmh
 * @version 1.0
 * @date 2022/11/28 20:39
 */
@Repository
public class ShopLogisticsDao {
    private static final Logger logger = LoggerFactory.getLogger(ShopLogisticsDao.class);
    public static final String KEY = "S%d";
    private RedisUtil redisUtil;

    private LogisticsDao logisticsDao;

    private UndeliverableDao undeliverableDao;

    private ShopLogisticsPoMapper shopLogisticsPoMapper;
    private RegionDao regionDao;

    @Autowired
    public ShopLogisticsDao(RedisUtil redisUtil, LogisticsDao logisticsDao, UndeliverableDao undeliverableDao, ShopLogisticsPoMapper shopLogisticsPoMapper, RegionDao regionDao) {
        this.redisUtil = redisUtil;
        this.logisticsDao = logisticsDao;
        this.undeliverableDao = undeliverableDao;
        this.shopLogisticsPoMapper = shopLogisticsPoMapper;
        this.regionDao = regionDao;
    }




    private ShopLogistics getBo(ShopLogisticsPo po){
        ShopLogistics ret;
        logger.debug("getBo po = {}", po);
        ret = cloneObj(po, ShopLogistics.class);
        this.setBo(ret);
        logger.debug("getBo ret = {}", ret);
        return ret;
    }

    private void setBo(ShopLogistics bo) {
        bo.setLogisticsDao(this.logisticsDao);
        bo.setUndeliverableDao(this.undeliverableDao);
        bo.setRegionDao(this.regionDao);
    }

    /**
     * 按照主键获得商户物流对象
     * @param id
     * @return
     * @throws RuntimeException
     */
    public ShopLogistics findById(Long id) throws RuntimeException {
        ShopLogistics ShopLogistics = null;
        if (null != id) {
            ShopLogisticsPo po = shopLogisticsPoMapper.selectByPrimaryKey(id);
            if (null == po) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺物流", id));
            }
            ShopLogistics = this.getBo(po);
        }
        return ShopLogistics;
    }


    /**
     *
     * @param page 页码
     * @param pageSize 页大小
     * @return
     */
    public PageInfo<ShopLogistics> retrieveValid(Integer page, Integer pageSize){
        List<ShopLogistics> ret =new ArrayList<>();
        ShopLogisticsPoExample example=new ShopLogisticsPoExample();
        ShopLogisticsPoExample.Criteria criteria=example.createCriteria();
        criteria.andInvalidEqualTo(ShopLogistics.VALID);
        PageHelper.startPage(page,pageSize,false);
        List<ShopLogisticsPo> poList=this.shopLogisticsPoMapper.selectByExample(example);
        if(poList.size()>0){
            ret=poList.stream().map(po->this.getBo(po)).collect(Collectors.toList());
        }
        return new PageInfo<>(ret);
    }

    public List<ShopLogistics> retrieveByShopId(Long shopId, Integer page, Integer pageSize)throws RuntimeException{
        List<ShopLogistics> ret =new ArrayList<>();
        if(null!=shopId){
            ShopLogisticsPoExample example=new ShopLogisticsPoExample();
            example.setOrderByClause("priority asc");
            ShopLogisticsPoExample.Criteria criteria=example.createCriteria();
            criteria.andShopIdEqualTo(shopId);
            PageHelper.startPage(page,pageSize,false);
            List<ShopLogisticsPo> poList=this.shopLogisticsPoMapper.selectByExample(example);
            if(poList.size()>0){
                ret=poList.stream().map(po->this.getBo(po)).collect(Collectors.toList());
            }
        }
        return ret;
    }

    /**
     * 更新地区信息
     * @param
     * @param user
     * @return
     */
    public ReturnObject saveById(ShopLogistics shopLogistics, UserDto user){
        if(null!=shopLogistics&&null!=shopLogistics.getId()){
            ShopLogisticsPo po=cloneObj(shopLogistics,ShopLogisticsPo.class);
            if (null != user) {
                putUserFields(po, "modifier", user);
                putGmtFields(po, "modified");
            }
            int ret= shopLogisticsPoMapper.updateByPrimaryKeySelective(po);
            if(0==ret){
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"商铺物流", po.getId()));
            }
        }
        return new ReturnObject(ReturnNo.OK);
    }


    /**
     * 增加地区信息
     * @param shopLogistics
     * @param user
     * @return
     * @throws RuntimeException
     */
    public ReturnObject save(ShopLogistics shopLogistics,UserDto user) throws RuntimeException{
        if(null == user){
            return new ReturnObject(ReturnNo.AUTH_NEED_LOGIN);
        }
        logger.debug("insertObj: shopLogistics = {}", shopLogistics);
        ShopLogisticsPo po = cloneObj(shopLogistics,ShopLogisticsPo.class);
        putUserFields(po, "creator", user);
        putGmtFields(po, "create");
        logger.debug("insertObj: po = {}", po);
        int ret = shopLogisticsPoMapper.insertSelective(po);
        if(ret==0){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺物流", shopLogistics.getId()));
        }
        shopLogistics.setId(po.getId());
        return new ReturnObject(ReturnNo.OK);
    }

}
