package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.controller.vo.WarehouseVo;
import cn.edu.xmu.oomall.freight.dao.RegionDao;
import cn.edu.xmu.oomall.freight.dao.WarehouseDao;
import cn.edu.xmu.oomall.freight.dao.bo.Region;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.service.dto.*;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

@Service
public class WarehouseService {
    private WarehouseDao warehouseDao;
    private RegionDao regionDao;
    private static final Logger logger = LoggerFactory.getLogger(WarehouseService.class);

    @Autowired
    public WarehouseService(WarehouseDao warehouseDao, RegionDao regionDao) {
        this.warehouseDao = warehouseDao;
        this.regionDao = regionDao;
    }

    @Transactional(rollbackFor = Exception.class)
    public WareHouseDto createWarehouses(Long shopId, UserDto user, WarehouseVo warehouseVo) {
        Warehouse warehouse = new Warehouse();
        warehouse.setShopId(shopId);
        warehouse.setGmtModified(LocalDateTime.now());
        warehouse.setGmtCreate(LocalDateTime.now());
        warehouse.setModifierId(user.getId());
        warehouse.setCreatorId(user.getId());
        warehouse.setCreatorName(user.getName());
        warehouse.setModifierName(user.getName());
        warehouse.setSenderName(warehouseVo.getSenderName());
        warehouse.setSenderMobile(warehouseVo.getSenderMobile());
        warehouse.setRegionId(warehouseVo.getRegionId());
        warehouse.setAddress(warehouseVo.getAddress());
        warehouse.setName(warehouseVo.getName());
        warehouse.setPriority(warehouseVo.getPriority());

        warehouse.setInvalid(Warehouse.VALID);

        //将自增id填充
        warehouseDao.save(warehouse, user);
        Region region = regionDao.findById(warehouseVo.getRegionId());

        RegionDto regionDto = cloneObj(region,RegionDto.class);

        WareHouseDto dto = cloneObj(warehouse, WareHouseDto.class);
        dto.setRegion(regionDto);
        dto.setCreator(user);
        dto.setModifier(user);

        return dto;
    }

    @Transactional
    public PageDto<WareHouseDto> retrieveWarehouses(Long shopId, Integer page, Integer pageSize, UserDto userDto) {
        PageInfo<Warehouse> warehousePageInfo = warehouseDao.retrieveByShopId(shopId, page, pageSize);
        List<WareHouseDto> wareHouseDtos = warehousePageInfo.getList().stream().map(
                warehouse -> {
                    Region region = regionDao.findById(warehouse.getRegionId());
                    RegionDto regionDto = cloneObj(region, RegionDto.class);
                    UserDto modifier = new UserDto();
                    modifier.setId(region.getModifierId());
                    modifier.setName(region.getModifierName());

                    UserDto creator = new UserDto();
                    creator.setId(region.getCreatorId());
                    creator.setName(region.getCreatorName());

                    WareHouseDto dto = cloneObj(warehouse, WareHouseDto.class);
                    dto.setCreator(creator);
                    dto.setModifier(modifier);
                    dto.setRegion(regionDto);
                    return dto;
                }
        ).collect(Collectors.toList());

        return new PageDto<>(wareHouseDtos, 0, page, pageSize, 0);
    }

    @Transactional(rollbackFor = Exception.class)
    public WareHouseDto updateWarehouses(Long shopId, Long id, UserDto userDto, WarehouseVo warehouseVo) {
        Warehouse warehouse = warehouseDao.findById(id);

        warehouse.setName(warehouseVo.getName());
        warehouse.setAddress(warehouseVo.getAddress());
        warehouse.setRegionId(warehouseVo.getRegionId());
        warehouse.setSenderName(warehouseVo.getSenderName());
        warehouse.setSenderMobile(warehouseVo.getSenderMobile());

        warehouseDao.saveById(warehouse, userDto);

        warehouse = warehouseDao.findById(warehouse.getId());
        WareHouseDto dto = cloneObj(warehouse, WareHouseDto.class);
        Region region = regionDao.findById(warehouse.getRegionId());
        RegionDto regionDto = cloneObj(region, RegionDto.class);

        UserDto creator = new UserDto();
        creator.setId(warehouse.getCreatorId());
        creator.setName(warehouse.getCreatorName());
        UserDto modifier = new UserDto();
        modifier.setId(warehouse.getModifierId());
        modifier.setName(warehouse.getModifierName());

        dto.setRegion(regionDto);
        dto.setCreator(creator);
        dto.setModifier(modifier);

        return dto;
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject delWarehouses(Long shopId, Long id) {
        return warehouseDao.delById(warehouseDao.findById(id));
    }

    /**
     * 返回某个地区可以配送的所有仓库
     * @param shopId
     * @param id 地区id
     * @param page
     * @param pageSize
     * @param userDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public PageDto<WarehousesDto> retrieveWarehouse(Long shopId,Long id,Integer page,Integer pageSize,UserDto userDto){
        Warehouse warehouse=warehouseDao.findByRegionId(id);
        List<WarehousesDto> warehousesDto = warehouseDao.retrieveByRegionId(id,page,pageSize)
                .stream()
                .map(warehouses -> {
                    SimpleWarehouseDto simpleWarehouseDto=new SimpleWarehouseDto();
                    simpleWarehouseDto.setId(warehouse.getId());
                    simpleWarehouseDto.setName(warehouse.getName());
                    simpleWarehouseDto.setInvalid(warehouse.getInvalid());
                    simpleWarehouseDto.setPriority(warehouse.getPriority());

                    SimpleUserDto creator = new SimpleUserDto();
                    creator.setId(warehouse.getCreatorId());
                    creator.setUserName(warehouse.getCreatorName());

                    SimpleUserDto modifier = new SimpleUserDto();
                    modifier.setId(warehouse.getModifierId());
                    modifier.setUserName(warehouse.getModifierName());

                    WarehousesDto dto = cloneObj(warehouses, WarehousesDto.class);
                    dto.setWarehouse(simpleWarehouseDto);
                    dto.setBeginTime(warehouse.getGmtCreate());
                    dto.setEndTime(warehouse.getGmtModified());
                    dto.setCreator(creator);
                    dto.setModifier(modifier);
                    return dto;
                }
        ).collect(Collectors.toList());
        logger.debug("retrieveWarehouses: pageDto = {}", warehousesDto);
        return new PageDto<WarehousesDto>(warehousesDto, 0, page, pageSize, 0);
    }
}
