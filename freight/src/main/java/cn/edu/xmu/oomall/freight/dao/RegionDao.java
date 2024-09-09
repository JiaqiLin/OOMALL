package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.dao.bo.Region;
import cn.edu.xmu.oomall.freight.mapper.generator.RegionPoMapper;
import cn.edu.xmu.oomall.freight.mapper.generator.po.RegionPo;
import cn.edu.xmu.oomall.freight.mapper.generator.po.RegionPoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

/**
 * ClassName RegionDao
 * Description  TODO
 *
 * @author Mr_X
 * @version 1.0
 * @date 2022/11/28 18:39
 */
@Repository
public class RegionDao {
    private static final Logger logger = LoggerFactory.getLogger(RegionDao.class);
    public static final String KEY = "R%d";

    private final static String PARENT_KEY = "RP%d";
    private final RegionPoMapper regionPoMapper;
    private final RedisUtil redisUtil;

    @Autowired
    public RegionDao(RegionPoMapper regionPoMapper, RedisUtil redisUtil) {
        this.regionPoMapper = regionPoMapper;
        this.redisUtil = redisUtil;
    }

    public void setBo(Region bo) {
        bo.setRegionDao(RegionDao.this);
    }

    public Region getBo(RegionPo po, Optional<String> redisKey) {
        Region bo = cloneObj(po, Region.class);
        this.setBo(bo);
        redisKey.ifPresent(key -> redisUtil.set(key, bo, -1));
        return bo;
    }

    /**
     * 按照主键获得地区对象
     * @param id
     * @return
     * @throws RuntimeException
     */
//    public Region findById(Long id) throws RuntimeException {
//        Region region = null;
//        if (null != id) {
//            logger.debug("findObjById: id = {}",id);
//            String key = String.format(KEY, id);
//            if (redisUtil.hasKey(key)) {
//                System.out.println("redis has the key\t" + key);
//                region = (Region) redisUtil.get(key);
//            } else {
//                RegionPo po = this.regionPoMapper.selectByPrimaryKey(id);
//                if (null == po) {
//                    throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "获取地区", id));
//                }
//                region = cloneObj(po, Region.class);
//                //永不过期
//                redisUtil.set(key, region, -1);
//            }
//        }
//        return region;
//    }


    /**
     * 通过id查找地区
     *
     * @param id id
     * @return Region
     * @throws RuntimeException
     */
    public Region findById(Long id) throws RuntimeException {
        logger.debug("findById: id = {}", id);
        if (null == id) {
            return null;
        }
        String key = String.format(KEY, id);
        if (redisUtil.hasKey(key)) {
            Region bo = (Region) redisUtil.get(key);
            this.setBo(bo);
            return bo;
        }
        RegionPo ret = regionPoMapper.selectByPrimaryKey(id);
        if (ret!=null) {
            return this.getBo(ret, Optional.of(key));
        } else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "地区", id));
        }
    }

    /**
     * 查询所有有效的地区
     * @param page 页码
     * @param pageSize 页大小
     * @return
     */
    public PageInfo<Region> retrieveValid(Integer page, Integer pageSize){
        List<Region> ret;
        RegionPoExample regionPoExample = new RegionPoExample();
        RegionPoExample.Criteria criteria = regionPoExample.createCriteria();
        PageHelper.startPage(page, pageSize, false);
        List<RegionPo> validRegions = regionPoMapper.selectByExample(regionPoExample);

        if(null != validRegions && validRegions.size() > 0){
            // 流编程
            ret = validRegions.stream().map(po -> cloneObj(po, Region.class)).collect(Collectors.toList());
        } else {
            ret = new ArrayList<>();
        }
        return new PageInfo<>(ret);
    }


    /**
     * 更新地区信息
     * @param region
     * @param user
     * @return
     */
    public ReturnObject saveById(Region region, UserDto user){
        if(null == user){
            return new ReturnObject(ReturnNo.AUTH_NEED_LOGIN);
        }
        RegionPo po = cloneObj(region, RegionPo.class);
        putUserFields(po, "modifier", user);
        putGmtFields(po, "Modified");
        int ret = regionPoMapper.updateByPrimaryKeySelective(po);
        if(0 == ret){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "地区信息",po.getId()));
        }
        // 由于redis无法回滚，必须使其保持和数据库的一致性
        redisUtil.del(String.format(RegionDao.KEY, po.getId()));

        return new ReturnObject(ReturnNo.OK);
    }


    /**
     * 增加地区信息
     * @param region
     * @param user
     * @return
     * @throws RuntimeException
     */
    public ReturnObject save(Region region,UserDto user) throws RuntimeException{
        if(null == user){
            return new ReturnObject(ReturnNo.AUTH_NEED_LOGIN);
        }
        logger.debug("insertObj: region = {}", region);
        RegionPo po = cloneObj(region,RegionPo.class);
        putUserFields(po, "creator", user);
        putGmtFields(po, "create");
        logger.debug("insertObj: po = {}", po);
        int ret = regionPoMapper.insertSelective(po);
        if(ret==0){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "地区", region.getId()));
        }
        region.setId(po.getId());
        return new ReturnObject(ReturnNo.OK);
    }

    public InternalReturnObject<List<Region>> retrieveParentsByRegionId(Long id){
        List<Region> ret=new ArrayList<>();
        if(null == id) {
            return null;
        }
        String key = String.format(PARENT_KEY, id);
        if(redisUtil.hasKey(key)) {
            List<Long> parentIds = (List<Long>) redisUtil.get(key);
            ret= parentIds.stream().map(this::findById).filter(Objects::nonNull).collect(Collectors.toList());
        }
        else {
            Region region = this.findById(id);
            region.setRegionDao(this);
            while(ret.size() < 10 &&!region.getPid().equals(Long.valueOf("-1"))) {
                region = region.getParentRegion();
                if(region==null) break;
                ret.add(region);
            }
            this.redisUtil.set(key, (ArrayList<Long>) ret.stream().map(Region::getId).collect(Collectors.toList()), -1);
        }

        return new InternalReturnObject<>(ret);
    }

}
