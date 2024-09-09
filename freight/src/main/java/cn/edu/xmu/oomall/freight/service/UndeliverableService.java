package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.controller.vo.UndeliverableVo;
import cn.edu.xmu.oomall.freight.dao.RegionDao;
import cn.edu.xmu.oomall.freight.dao.UndeliverableDao;
import cn.edu.xmu.oomall.freight.dao.bo.Region;
import cn.edu.xmu.oomall.freight.dao.bo.Undeliverable;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.service.dto.RegionDto;
import cn.edu.xmu.oomall.freight.service.dto.UndeliverableDto;
import cn.edu.xmu.oomall.freight.service.dto.WareHouseDto;
import cn.edu.xmu.oomall.freight.service.util.TimeFormatter;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;
import static cn.edu.xmu.oomall.freight.service.util.TimeFormatter.StrToLocalDateTime;

@Service
public class UndeliverableService {
    private UndeliverableDao undeliverableDao;

    private RegionDao regionDao;
    private static final Logger logger = LoggerFactory.getLogger(UndeliverableService.class);
    @Autowired
    public UndeliverableService(UndeliverableDao undeliverableDao,RegionDao regionDao){
        this.undeliverableDao=undeliverableDao;
        this.regionDao=regionDao;
    }
    /**
     *商铺查询快递公司无法配送的地区
     */
    @Transactional
    public PageDto<UndeliverableDto> retrieveUndeliverableRegions(Long shopLogisticsId, Integer page, Integer pageSize, UserDto userDto) {
        List<UndeliverableDto> dtos = undeliverableDao.retrieveByShopLogisticsId(shopLogisticsId, page, pageSize)
                .stream()
                .map(undeliverable -> {
                    RegionDto regionDto = new RegionDto();
                    regionDto.setId(undeliverable.getRegionId());
                    regionDto.setName(undeliverable.getRegion().getName());
                    UndeliverableDto undeliverableDto = cloneObj(undeliverable, UndeliverableDto.class);
                    undeliverableDto.setRegionDto(regionDto);
                    return undeliverableDto;
                }).collect(Collectors.toList());

        logger.debug("retrieveUndeliverableRegions: pageDto = {}", dtos);
        return new PageDto<UndeliverableDto>(dtos,0,page,pageSize,0);
    }

    /**
     * 商铺指定不可达地区
     * @param regionId
     * @param shopLogisticsId
     * @param undeliverableVo
     * @param userDto
     * @return
     */
    @Transactional
    public UndeliverableDto createUndeliverable(Long regionId, Long shopLogisticsId,UndeliverableVo undeliverableVo,UserDto userDto){
        Undeliverable bo = new Undeliverable();

        bo.setRegionId(regionId);
        bo.setShopLogisticsId(shopLogisticsId);
        bo.setEndTime(StrToLocalDateTime(undeliverableVo.getEndTime()));
        bo.setBeginTime(StrToLocalDateTime(undeliverableVo.getBeginTime()));

        undeliverableDao.save(bo,userDto);

        bo=undeliverableDao.findById(bo.getId());
        bo.getRegion();

        RegionDto regionDto = RegionDto.builder().id(bo.getRegionId()).name(bo.getRegion().getName()).build();
        UndeliverableDto dto=UndeliverableDto.builder().beginTime(bo.getBeginTime()).endTime(bo.getEndTime())
                .creator(userDto).gmtCreate(LocalDateTime.now()).regionDto(regionDto).build();
        logger.debug("createUndeliverable: Dto = {}", dto);
        return dto;
    }

    /**
     * 商铺更新不可达信息
     * @param regionId
     * @param shopLogisticsId
     * @param undeliverableVo
     * @param userDto
     * @return
     */
    public UndeliverableDto updateUndeliverable(Long regionId, Long shopLogisticsId,UndeliverableVo undeliverableVo,UserDto userDto){
        Undeliverable bo = undeliverableDao.findByRegionIdAndShopLogisticsId(regionId,shopLogisticsId);
        bo.setRegionId(regionId);
        bo.setShopLogisticsId(shopLogisticsId);
        bo.setEndTime(StrToLocalDateTime(undeliverableVo.getEndTime()));
        bo.setBeginTime(StrToLocalDateTime(undeliverableVo.getBeginTime()));

        undeliverableDao.saveById(bo,userDto);
        bo=undeliverableDao.findById(bo.getId());//不一定需要这一句，findByRegionIdAndShopLogisticsId注入过id了
        bo.getRegion();

        RegionDto regionDto = RegionDto.builder().id(bo.getRegionId()).name(bo.getRegion().getName()).build();
        UndeliverableDto dto=UndeliverableDto.builder().beginTime(bo.getBeginTime()).endTime(bo.getEndTime())
                .regionDto(regionDto).modifier(new UserDto(bo.getModifierId(),bo.getModifierName(),null,null)).build();
        return dto;
    }

    /**
     * 商铺删除不可达信息
     * @param regionId
     * @param shopLogisticsId
     * @param userDto
     */
    public void delUndeliverable(Long regionId, Long shopLogisticsId, UserDto userDto) {
        Undeliverable bo = undeliverableDao.findByRegionIdAndShopLogisticsId(Long.valueOf(regionId),shopLogisticsId);
        undeliverableDao.delById(bo);
    }
}
