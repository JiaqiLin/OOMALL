package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.Constants;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.controller.vo.WarehouseRegionUpdateVo;
import cn.edu.xmu.oomall.freight.controller.vo.WarehouseRegionVo;
import cn.edu.xmu.oomall.freight.dao.RegionDao;
import cn.edu.xmu.oomall.freight.dao.WarehouseDao;
import cn.edu.xmu.oomall.freight.dao.WarehouseRegionDao;
import cn.edu.xmu.oomall.freight.dao.bo.Region;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseRegion;
import cn.edu.xmu.oomall.freight.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;
import static cn.edu.xmu.oomall.freight.service.util.TimeFormatter.StrToLocalDateTime;


@Service
public class WarehouseRegionService {

    private static final Logger logger= LoggerFactory.getLogger(WarehouseRegionService.class);

    private WarehouseRegionDao warehouseRegionDao;

    private RegionDao regionDao;

    private WarehouseDao warehouseDao;

    @Autowired
    public WarehouseRegionService(WarehouseRegionDao warehouseRegionDao,RegionDao regionDao,WarehouseDao warehouseDao){
        this.warehouseRegionDao=warehouseRegionDao;
        this.regionDao=regionDao;
        this.warehouseDao=warehouseDao;
    }

    @Transactional(rollbackFor = Exception.class)
    public WarehouseRegionDto createWarehouseRegion(Long shopId, Long wid, Long id, WarehouseRegionUpdateVo warehouseRegionVo, UserDto user){
        //判断该地区和仓库是否存在
        if(null==regionDao.findById(id))
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "地区不存在", id));
        if(null==warehouseDao.findById(wid))
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "仓库不存在", wid));
        //重复出997错误
        List<WarehouseRegion> ret=warehouseRegionDao.findSame(id,wid);
        if(ret!=null){
            throw new BusinessException(ReturnNo.FREIGHT_WAREHOUSEREGION_EXIST,String.format(ReturnNo.FREIGHT_WAREHOUSEREGION_EXIST.getMessage(),"重复设置地区",id,wid));
        }
        WarehouseRegion warehouseRegion=new WarehouseRegion(wid,id,StrToLocalDateTime(warehouseRegionVo.getBeginTime()),StrToLocalDateTime(warehouseRegionVo.getEndTime()));
        warehouseRegion.setCreatorId(user.getId());
        warehouseRegion.setCreatorName(user.getName());
        warehouseRegion.setGmtCreate(LocalDateTime.now());
        warehouseRegionDao.save(warehouseRegion,user);
        warehouseRegion=warehouseRegionDao.findById(warehouseRegion.getId());
        WarehouseRegionDto warehouseRegionDto=cloneObj(warehouseRegion,WarehouseRegionDto.class);
        SimpleUserDto userDto=new SimpleUserDto(user.getId(),user.getName());
        warehouseRegionDto.setCreator(userDto);
        return warehouseRegionDto;
    }

    @Transactional(rollbackFor = Exception.class)
    public WarehouseRegionDto updateWarehouseRegion(Long shopId, Long wid, Long id, WarehouseRegionUpdateVo warehouseRegionVo, UserDto user){
        WarehouseRegion warehouseRegion=warehouseRegionDao.findByRegionIdAndWarehouseId(id,wid);
        if(null!=warehouseRegion){
        warehouseRegion.setBeginTime(StrToLocalDateTime(warehouseRegionVo.getBeginTime()));
        warehouseRegion.setEndTime(StrToLocalDateTime(warehouseRegionVo.getEndTime()));
        warehouseRegion.setGmtModified(LocalDateTime.now());
        warehouseRegion.setModifierId(user.getId());
        warehouseRegion.setModifierName(user.getName());
        warehouseRegionDao.saveById(warehouseRegion,user);
        }
        warehouseRegion=warehouseRegionDao.findById(warehouseRegion.getId());
        WarehouseRegionDto warehouseRegionDto=cloneObj(warehouseRegion,WarehouseRegionDto.class);
        SimpleUserDto userDto=new SimpleUserDto(user.getId(),user.getName());
        warehouseRegionDto.setModifier(userDto);
        return warehouseRegionDto;
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject delWarehouseRegion(Long shopId, Long wid, Long id,UserDto user){
        return warehouseRegionDao.delById(warehouseRegionDao.findByRegionIdAndWarehouseId(id,wid));
    }

    /**
     * 返回某个仓库的配送地区
     * @param shopId
     * @param id 仓库id
     * @param page
     * @param pageSize
     * @param user
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public PageDto<WarehouseRegionDto> retrieveWarehouseRegion(Long shopId,Long id,Integer page,Integer pageSize,UserDto user){
        WarehouseRegion warehouseRegion=this.warehouseRegionDao.findById(id);
        List<WarehouseRegionDto> warehouseRegionDto = warehouseRegionDao.retrieveByWarehouseId(id,page,pageSize)
                        .stream()
                        .map(warehouseRegions->{
                            Region region=regionDao.findById(warehouseRegion.getRegionId());
                            RegionDto regionDto=new RegionDto();
                            regionDto.setId(warehouseRegion.getRegionId());
                            regionDto.setName(region.getName());

                            SimpleUserDto creator = new SimpleUserDto();
                            creator.setId(warehouseRegion.getCreatorId());
                            creator.setUserName(warehouseRegion.getCreatorName());

                            SimpleUserDto modifier = new SimpleUserDto();
                            modifier.setId(warehouseRegion.getModifierId());
                            modifier.setUserName(warehouseRegion.getModifierName());

                            WarehouseRegionDto dto=cloneObj(warehouseRegions,WarehouseRegionDto.class);
                            dto.setRegion(regionDto);
                            dto.setBeginTime(warehouseRegion.getBeginTime());
                            dto.setEndTime(warehouseRegion.getEndTime());
                            dto.setCreator(creator);
                            dto.setModifier(modifier);
                            dto.setGmtCreate(warehouseRegion.getGmtCreate());
                            dto.setGmtModified(warehouseRegion.getGmtModified());
                            return dto;
                        }
                        ).collect(Collectors.toList());

        logger.debug("retrieveWarehouseRegions: pageDto = {}",warehouseRegionDto);
        return new PageDto<>(warehouseRegionDto, 0, page, pageSize, 0);
    }
}
