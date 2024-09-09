package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.controller.vo.ShopLogisticsVo;
import cn.edu.xmu.oomall.freight.dao.LogisticsDao;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.service.dto.LogisticsDto;
import cn.edu.xmu.oomall.freight.service.dto.ShopLogisticsDto;
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
import static cn.edu.xmu.javaee.core.util.Common.putGmtFields;

@Service
public class ShopLogisticsService {
    private ShopLogisticsDao shopLogisticsDao;
    private LogisticsDao logisticsDao;

    private static final Logger logger = LoggerFactory.getLogger(WarehouseService.class);

    @Autowired
    public ShopLogisticsService(ShopLogisticsDao shopLogisticsDao, LogisticsDao logisticsDao){
        this.shopLogisticsDao = shopLogisticsDao;
        this.logisticsDao = logisticsDao;
    }

    /**
     * 店家新增物流合作
     * @param shopId
     * @param user
     * @param shopLogisticsVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ShopLogisticsDto createShopLogistics(Long shopId, UserDto user, ShopLogisticsVo shopLogisticsVo){
        List<ShopLogistics> log = shopLogisticsDao.retrieveByShopId(shopId, 0, 10);
        for(ShopLogistics s : log){
            if(s.getLogisticsId().equals(shopLogisticsVo.getLogisticsId())){
                throw new BusinessException(ReturnNo.FREIGHT_LOGISTIC_EXIST);
            }
        }
        ShopLogistics shopLogistics = new ShopLogistics();
        shopLogistics.setLogisticsId(shopLogisticsVo.getLogisticsId());
        shopLogistics.setSecret(shopLogisticsVo.getSecret());
        shopLogistics.setPriority(shopLogisticsVo.getPriority());
        shopLogistics.setShopId(shopId);
        shopLogistics.setInvalid(ShopLogistics.VALID);
        shopLogistics.setGmtCreate(LocalDateTime.now());
        shopLogistics.setCreatorId(user.getId());
        shopLogistics.setCreatorName(user.getName());

        shopLogisticsDao.save(shopLogistics, user);

        shopLogistics = shopLogisticsDao.findById(shopLogistics.getId());

        ShopLogisticsDto shopLogisticsDto = cloneObj(shopLogistics, ShopLogisticsDto.class);
        Logistics logistics = logisticsDao.findById(shopLogisticsVo.getLogisticsId());
        LogisticsDto logisticsDto = cloneObj(logistics, LogisticsDto.class);

        shopLogisticsDto.setLogistics(logisticsDto);
        shopLogisticsDto.setCreator(user);

        return shopLogisticsDto;
    }

    /**
     * 店家获得物流合作信息
     * @param shopId
     * @param page
     * @param pageSize
     * @param userDto
     * @return
     */
    @Transactional
    public PageDto<ShopLogisticsDto> retrieveShopLogistics(Long shopId, Integer page, Integer pageSize, UserDto userDto) {
        List<ShopLogisticsDto> dtos = shopLogisticsDao.retrieveByShopId(shopId, page, pageSize)
                .stream()
                .map(shopLogistics -> {
                    Logistics logistics = logisticsDao.findById(shopLogistics.getLogisticsId());
                    LogisticsDto logisticsDto = cloneObj(logistics, LogisticsDto.class);
                    UserDto creator = new UserDto();
                    creator.setId(shopLogistics.getCreatorId());
                    creator.setName(shopLogistics.getCreatorName());
                    UserDto modifier = new UserDto();
                    modifier.setId(shopLogistics.getModifierId());
                    modifier.setName(shopLogistics.getModifierName());
                    ShopLogisticsDto shopLogisticsDto = cloneObj(shopLogistics, ShopLogisticsDto.class);
                    shopLogisticsDto.setLogistics(logisticsDto);
                    shopLogisticsDto.setCreator(creator);
                    shopLogisticsDto.setModifier(modifier);
                    return shopLogisticsDto;
                }).collect(Collectors.toList());


        logger.debug("retrieveShopLogistics: pageDto = {}", dtos);
        return new PageDto<ShopLogisticsDto>(dtos, 0, page, pageSize, 0);
    }

    /**
     * 店家更新物流合作信息
     * @param shopId
     * @param id
     * @param userDto
     * @param shopLogisticsVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ShopLogisticsDto updateShopLogistics(Long shopId, Long id, UserDto userDto, ShopLogisticsVo shopLogisticsVo) {
        ShopLogistics shopLogistics = shopLogisticsDao.findById(id);
        shopLogistics.setPriority(shopLogisticsVo.getPriority());
        shopLogistics.setSecret(shopLogisticsVo.getSecret());
        shopLogistics.setGmtModified(LocalDateTime.now());
        shopLogistics.setModifierId(userDto.getId());
        shopLogistics.setModifierName(userDto.getName());

        shopLogisticsDao.saveById(shopLogistics, userDto);
        shopLogistics = shopLogisticsDao.findById(shopLogistics.getId());

        ShopLogisticsDto shopLogisticsDto = cloneObj(shopLogistics, ShopLogisticsDto.class);
        Logistics logistics = logisticsDao.findById(shopLogistics.getId());
        LogisticsDto logisticsDto = cloneObj(logistics, LogisticsDto.class);
        UserDto creator = new UserDto();
        creator.setId(shopLogistics.getCreatorId());
        creator.setName(shopLogistics.getCreatorName());
        UserDto modifier = new UserDto();
        modifier.setId(shopLogistics.getModifierId());
        modifier.setName(shopLogistics.getModifierName());

        shopLogisticsDto.setLogistics(logisticsDto);
        shopLogisticsDto.setCreator(creator);
        shopLogisticsDto.setModifier(modifier);
        return shopLogisticsDto;
    }

    /**
     * 商铺停用某个物流合作
     * @param shopId
     * @param id
     * @param userDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ShopLogisticsDto suspendShopLogistics(Long shopId, Long id, UserDto userDto) {
        ShopLogistics shopLogistics = shopLogisticsDao.findById(id);

        shopLogistics.setInvalid(ShopLogistics.INVALID);
        shopLogistics.setModifierName(userDto.getName());
        shopLogistics.setModifierId(userDto.getId());
        shopLogistics.setGmtModified(LocalDateTime.now());

        shopLogisticsDao.saveById(shopLogistics, userDto);
        shopLogistics = shopLogisticsDao.findById(shopLogistics.getId());

        ShopLogisticsDto shopLogisticsDto = cloneObj(shopLogistics, ShopLogisticsDto.class);
        Logistics logistics = logisticsDao.findById(shopLogistics.getId());
        LogisticsDto logisticsDto = cloneObj(logistics, LogisticsDto.class);
        UserDto creator = new UserDto();
        creator.setId(shopLogistics.getCreatorId());
        creator.setName(shopLogistics.getCreatorName());
        UserDto modifier = new UserDto();
        modifier.setId(shopLogistics.getModifierId());
        modifier.setName(shopLogistics.getModifierName());

        shopLogisticsDto.setLogistics(logisticsDto);
        shopLogisticsDto.setCreator(creator);
        shopLogisticsDto.setModifier(modifier);
        return shopLogisticsDto;
    }

    /**
     * 商铺恢复某个物流合作
     * @param shopId
     * @param id
     * @param userDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ShopLogisticsDto resumeShopLogistics(Long shopId, Long id, UserDto userDto) {
        ShopLogistics shopLogistics = shopLogisticsDao.findById(id);

        shopLogistics.setInvalid(ShopLogistics.VALID);
        shopLogistics.setModifierName(userDto.getName());
        shopLogistics.setModifierId(userDto.getId());
        shopLogistics.setGmtModified(LocalDateTime.now());

        shopLogisticsDao.saveById(shopLogistics, userDto);
        shopLogistics = shopLogisticsDao.findById(shopLogistics.getId());

        ShopLogisticsDto shopLogisticsDto = cloneObj(shopLogistics, ShopLogisticsDto.class);
        Logistics logistics = logisticsDao.findById(shopLogistics.getId());
        LogisticsDto logisticsDto = cloneObj(logistics, LogisticsDto.class);
        UserDto creator = new UserDto();
        creator.setId(shopLogistics.getCreatorId());
        creator.setName(shopLogistics.getCreatorName());
        UserDto modifier = new UserDto();
        modifier.setId(shopLogistics.getModifierId());
        modifier.setName(shopLogistics.getModifierName());

        shopLogisticsDto.setLogistics(logisticsDto);
        shopLogisticsDto.setCreator(creator);
        shopLogisticsDto.setModifier(modifier);
        return shopLogisticsDto;
    }

}
