package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.controller.vo.WarehouseLogisticsInfoVo;
import cn.edu.xmu.oomall.freight.dao.LogisticsDao;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.WarehouseDao;
import cn.edu.xmu.oomall.freight.dao.WarehouseLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.bo.*;
import cn.edu.xmu.oomall.freight.service.dto.*;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.edu.xmu.oomall.freight.service.util.TimeFormatter.StrToLocalDateTime;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;
import static cn.edu.xmu.javaee.core.util.Common.putGmtFields;
import static cn.edu.xmu.oomall.freight.service.util.TimeFormatter.StrToLocalDateTime;

@Service
public class WarehouseLogisticsService {
    @Autowired
    private WarehouseLogisticsDao warehouseLogisticsDao;
    @Autowired
    private ShopLogisticsDao shopLogisticsDao;
    @Autowired
    private WarehouseDao warehouseDao;
    @Autowired
    private LogisticsDao logisticsDao;

    private static final Logger logger = LoggerFactory.getLogger(WarehouseService.class);


    @Transactional(rollbackFor = Exception.class)
    public WarehouseLogisticsDto createWarehouseLogistics(UserDto user, Long shopId, Long id, Long lid, WarehouseLogisticsInfoVo warehouseLogisticsInfoVo) {
        WarehouseLogistics warehouseLogistics = new WarehouseLogistics();
        warehouseLogistics.setShopId(shopId);
        warehouseLogistics.setWarehouseId(id);
        warehouseLogistics.setShopLogisticsId(lid);
        warehouseLogistics.setBeginTime(StrToLocalDateTime(warehouseLogisticsInfoVo.getBegintime()));
        warehouseLogistics.setEndTime(StrToLocalDateTime(warehouseLogisticsInfoVo.getEndtime()));

        warehouseLogistics.setGmtModified(LocalDateTime.now());
        warehouseLogistics.setGmtCreate(LocalDateTime.now());
        warehouseLogistics.setModifierId(user.getId());
        warehouseLogistics.setCreatorId(user.getId());
        warehouseLogistics.setCreatorName(user.getName());
        warehouseLogistics.setModifierName(user.getName());
        warehouseLogistics.setInvalid(WarehouseLogistics.VALID);

        warehouseLogisticsDao.save(warehouseLogistics, user);
        System.out.println("service.getWarehouseLogistics.ID=========\t" + warehouseLogistics.getId());
        ShopLogistics shopLogistics = shopLogisticsDao.findById(lid);
        Logistics logistics = logisticsDao.findById(shopLogistics.getLogisticsId());

        LogisticsDto logisticsDto = cloneObj(logistics, LogisticsDto.class);
        ShopLogisticsDto shopLogisticsDto = cloneObj(shopLogisticsDao.findById(lid), ShopLogisticsDto.class);
        WarehouseLogisticsDto dto = cloneObj(warehouseLogistics, WarehouseLogisticsDto.class);

        shopLogisticsDto.setLogistics(logisticsDto);
        dto.setShopLogistics(shopLogisticsDto);
        dto.setCreator(user);
        dto.setModifier(user);
        dto.setBeginTime(warehouseLogistics.getBeginTime());
        dto.setEndTime(warehouseLogistics.getEndTime());
        System.out.println("service.WarehouseLogisticsDto=========\t" + dto);

        return dto;
    }

    @Transactional(rollbackFor = Exception.class)
    public PageDto<WarehouseLogisticsDto> retrieveWarehouseLogistics(Long shopId, Integer page, Integer pageSize, UserDto user, Long id) {
        // 根据仓库id来查询
        PageInfo<WarehouseLogistics> warehouseLogisticsPageInfo = warehouseLogisticsDao.retrieveByWarehouseId(id, page, pageSize);
        List<WarehouseLogisticsDto> wareHouseLogisticsDtos = warehouseLogisticsPageInfo.getList().stream().map(
                warehouseLogistics -> {
                    ShopLogistics shoplogistics = shopLogisticsDao.findById(warehouseLogistics.getShopLogisticsId());
                    Logistics logistics = logisticsDao.findById(shoplogistics.getLogisticsId());

                    LogisticsDto logisticsDto = cloneObj(logistics, LogisticsDto.class);
                    ShopLogisticsDto shoplogisticsDto = cloneObj(shoplogistics, ShopLogisticsDto.class);

                    UserDto creator = new UserDto();
                    creator.setId(warehouseLogistics.getCreatorId());
                    creator.setName(warehouseLogistics.getCreatorName());

                    UserDto modifier = new UserDto();
                    modifier.setId(warehouseLogistics.getModifierId());
                    modifier.setName(warehouseLogistics.getModifierName());

                    WarehouseLogisticsDto dto = cloneObj(warehouseLogistics, WarehouseLogisticsDto.class);
                    dto.setCreator(creator);
                    dto.setModifier(modifier);
                    shoplogisticsDto.setLogistics(logisticsDto);
                    dto.setShopLogistics(shoplogisticsDto);
                    dto.setBeginTime(warehouseLogistics.getBeginTime());
                    dto.setEndTime(warehouseLogistics.getEndTime());
                    return dto;
                }
        ).collect(Collectors.toList());

        return new PageDto<>(wareHouseLogisticsDtos, 0, page, pageSize, 0);
    }

    @Transactional(rollbackFor = Exception.class)
    public WarehouseLogisticsDto updateWarehouseLogistics(UserDto user, Long shopId, Long id, Long lid, WarehouseLogisticsInfoVo warehouseLogisticsInfoVo) {
        WarehouseLogistics warehouseLogistics = warehouseLogisticsDao.findByLidAndWid(id, lid);

        warehouseLogistics.setBeginTime(StrToLocalDateTime(warehouseLogisticsInfoVo.getBegintime()));
        warehouseLogistics.setEndTime(StrToLocalDateTime(warehouseLogisticsInfoVo.getEndtime()));
        warehouseLogistics.setGmtModified(LocalDateTime.now());
        warehouseLogistics.setModifierId(user.getId());
        warehouseLogistics.setModifierName(user.getName());

        warehouseLogisticsDao.saveById(warehouseLogistics, user);

        warehouseLogistics = warehouseLogisticsDao.findById(warehouseLogistics.getId());

        WarehouseLogisticsDto warehouseLogisticsDto = cloneObj(warehouseLogistics, WarehouseLogisticsDto.class);

        ShopLogistics shoplogistics = shopLogisticsDao.findById(warehouseLogistics.getShopLogisticsId());

        Logistics logistics = logisticsDao.findById(shoplogistics.getLogisticsId());
        LogisticsDto logisticsDto = cloneObj(logistics, LogisticsDto.class);

        ShopLogisticsDto shoplogisticsDto = cloneObj(shoplogistics, ShopLogisticsDto.class);


        UserDto creator = new UserDto();
        creator.setId(warehouseLogistics.getCreatorId());
        creator.setName(warehouseLogistics.getCreatorName());
        UserDto modifier = new UserDto();
        modifier.setId(user.getId());
        modifier.setName(user.getName());

        shoplogisticsDto.setLogistics(logisticsDto);
        warehouseLogisticsDto.setShopLogistics(shoplogisticsDto);
        warehouseLogisticsDto.setCreator(creator);
        warehouseLogisticsDto.setModifier(modifier);
        warehouseLogisticsDto.setBeginTime(warehouseLogistics.getBeginTime());
        warehouseLogisticsDto.setEndTime(warehouseLogistics.getEndTime());
        return warehouseLogisticsDto;
    }

    /**
     * @description:
     * @param: user
     * shopId
     * id 仓库id
     * lid 仓库物流id
     * @return: cn.edu.xmu.javaee.core.model.ReturnObject
     * @author
     * @date: 21:15 2022/12/31
     */

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject delWarehouseLogistics(UserDto user, Long shopId, Long id, Long lid) {
        return warehouseLogisticsDao.delById(warehouseLogisticsDao.findByLidAndWid(id,lid));
    }


}
