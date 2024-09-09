package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.controller.vo.ExpressVo;
import cn.edu.xmu.oomall.freight.dao.ExpressDao;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.UndeliverableDao;
import cn.edu.xmu.oomall.freight.dao.bo.*;
import cn.edu.xmu.oomall.freight.service.courier.ExpressAdaptor;
import cn.edu.xmu.oomall.freight.service.courier.ExpressAdaptorFactory;
import cn.edu.xmu.oomall.freight.service.courier.dto.CancelExpressAdaptorDto;
import cn.edu.xmu.oomall.freight.service.courier.dto.GetExpressAdaptorDto;
import cn.edu.xmu.oomall.freight.service.courier.dto.PostExpressAdaptorDto;
import cn.edu.xmu.oomall.freight.service.dto.*;
import cn.edu.xmu.oomall.freight.service.responsibilityChain.*;
import io.lettuce.core.StrAlgoArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@Service
public class ExpressService {

    private LogisticsService logisticsService;
    private ShopLogisticsDao shopLogisticsDao;

    private ExpressDao expressDao;

    private UndeliverableDao undeliverableDao;

    private ExpressAdaptorFactory expressAdaptorFactory;
    private WarehouseRegionHandler warehouseRegionHandler;
    private WarehouseLogisticsHandler warehouseLogisticsHandler;
    private ShopLogisticsPriorityHandler shopLogisticsPriorityHandler;
    private WarehousePriorityHandler warehousePriorityHandler;
    private ResultHandler resultHandler;
    private  WarehouseHandler warehouseHandler;
    private static  final Logger logger = LoggerFactory.getLogger(ExpressDao.class);
    @Autowired
    public ExpressService(ShopLogisticsDao shopLogisticsDao, ExpressDao expressDao,UndeliverableDao undeliverableDao, ExpressAdaptorFactory expressAdaptorFactory,LogisticsService logisticsService,
                          WarehouseRegionHandler warehouseRegionHandler, WarehouseLogisticsHandler warehouseLogisticsHandler,
                          ShopLogisticsPriorityHandler shopLogisticsPriorityHandler, WarehousePriorityHandler warehousePriorityHandler,
                          ResultHandler resultHandler, WarehouseHandler warehouseHandler){
        this.shopLogisticsDao=shopLogisticsDao;
        this.expressDao=expressDao;
        this.undeliverableDao=undeliverableDao;
        this.logisticsService=logisticsService;
        this.expressAdaptorFactory=expressAdaptorFactory;
        this.warehouseRegionHandler = warehouseRegionHandler;
        this.warehouseLogisticsHandler = warehouseLogisticsHandler;
        this.shopLogisticsPriorityHandler = shopLogisticsPriorityHandler;
        this.warehousePriorityHandler = warehousePriorityHandler;
        this.resultHandler = resultHandler;
        this.warehouseHandler = warehouseHandler;
        warehouseHandler.setNext(warehouseRegionHandler);
        warehouseRegionHandler.setNext(warehouseLogisticsHandler);
        warehouseLogisticsHandler.setNext(shopLogisticsPriorityHandler);
        shopLogisticsPriorityHandler.setNext(warehousePriorityHandler);
        warehousePriorityHandler.setNext(resultHandler);
    }
    @Transactional
    public SimpleExpressDto createExpress(Long shopId, Express express, UserDto user){
        //获取物流
        ShopLogistics shopLogistics=shopLogisticsDao.findById(express.getShopLogisticsId());
        if(!shopLogistics.getShopId().equals(shopId)){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商户物流", shopLogistics.getId(), shopId));
        }
        logger.debug("createExpress: shopLogistics = {}", shopLogistics);
        //判断物流是否可用
        if (ShopLogistics.INVALID == shopLogistics.getInvalid()) {
            //TODO 物流不可用状态码
            throw new BusinessException(ReturnNo.PAY_CHANNEL_INVALID, String.format(ReturnNo.PAY_CHANNEL_INVALID.getMessage(), shopLogistics.getLogistics().getName()));
        }
        //判断物流是否不可达收货地
        Undeliverable undeliverable=undeliverableDao.findByRegionIdAndShopLogisticsId(express.getDeliverRegionId(),shopLogistics.getId());
        if (undeliverable != null) {
            //TODO 物流不可达状态码
            throw new BusinessException(ReturnNo.PAY_CHANNEL_INVALID, String.format(ReturnNo.PAY_CHANNEL_INVALID.getMessage(), shopLogistics.getLogistics().getName()));
        }
        express.setShopId(shopId);
        expressDao.save(express,user);
        express = expressDao.findById(express.getId());

        //找到对应渠道bean对象
        ExpressAdaptor expressAdaptor=this.expressAdaptorFactory.createExpressAdaptor(shopLogistics);
        logger.debug("createExpress: expressAdaptor = {}, express = {}", expressAdaptor, express);
        //下运单
        PostExpressAdaptorDto postExpressAdaptorDto= expressAdaptor.createExpress(express);
        //转回返回值
        SimpleExpressDto simpleExpressDto=new SimpleExpressDto();
        simpleExpressDto.setBillCode(postExpressAdaptorDto.getBillCode());
        simpleExpressDto.setId(express.getId());
        return simpleExpressDto;
    }
    @Transactional
    public ExpressDto searchExpressByBillCode(Long shopId,String billCode,UserDto user){
        //根据运单id查询运单
        Express express = expressDao.findByBillCode(billCode);
        logger.debug("searchExpressByBillCode: express = {}", express);
        if(!express.getShopId().equals(shopId)){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "运单billCode="+billCode,0 , shopId));
        }
        return searchExpressByDBExpress(express,user);
    }

    @Transactional
    public ExpressDto searchExpressByExpressId(Long expressId,UserDto user){
        //根据运单id查询运单
        Express express = expressDao.findById(expressId);
        logger.debug("searchExpressByExpressId: express = {}", express);
        return searchExpressByDBExpress(express,user);
    }

    @Transactional
    public void confirmExpressByExpressId(Long shopId,Long expressId,Integer status,UserDto user){
        //根据运单id查询运单
        Express express = expressDao.findById(expressId);
        logger.debug("confirmExpressByExpressId: express = {}", express);
        //判断是不是这家店的
        if(express.getShopId()!=shopId){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "运单", express.getId(), shopId));
        }
        Byte statusCode = status==0?Express.BROKEN:Express.RECYCLE;
        //判断状态可否改变
        if(!express.allowStatus(statusCode)){
            throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(), "运单",expressId,express.getStatusName()));
        }
        //改变状态
        express.setStatus(statusCode);
        expressDao.saveById(express,user);
    }

    @Transactional
    public void cancelExpressByExpressId(Long shopId,Long expressId,UserDto user){
        //根据运单id查询运单，获得运单号billcode，shopLogistics
        Express express = expressDao.findById(expressId);
        logger.debug("confirmExpressByExpressId: express = {}", express);
        if(express==null){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(),"运单", expressId));
        }
        //判断是不是这家店的
        if(express.getShopId()!=shopId){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "运单", express.getId(), shopId));
        }
        //判断状态可否改变
        if(!express.allowStatus(Express.CANCEL)){
            throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(), "运单",expressId,express.getStatusName()));
        }
        ShopLogistics shopLogistics=express.getShopLogistics();
        if (ShopLogistics.INVALID == shopLogistics.getInvalid()) {
            throw new BusinessException(ReturnNo.FREIGHT_SHOPLOGISTICS_INVALID, String.format(ReturnNo.FREIGHT_SHOPLOGISTICS_INVALID.getMessage(), shopLogistics.getId()));
        }
        //根据shopLogistics获得物流渠道bean对象
        ExpressAdaptor expressAdaptor=this.expressAdaptorFactory.createExpressAdaptor(shopLogistics);
        //根据运单号取消物流
        CancelExpressAdaptorDto cancelExpressAdaptorDto= expressAdaptor.cancelExpress(express);
        //改变数据库中记录状态
        express.setStatus(Express.CANCEL);
        expressDao.saveById(express,user);
    }

    @Transactional
    public SimpleExpressDto createExpressByPriority(Long shopId,Express express,UserDto user){
        Long regionId=express.getDeliverRegionId();
        List<Warehouse> warehouses=new ArrayList<>();
        warehouses=warehouseHandler.handle(warehouses,regionId,shopId);
        Warehouse warehouse=warehouses.get(0);
        express.setShopLogisticsId(warehouse.getWarehouseLogisticsList().get(0).getShopLogisticsId());
        express.setSenderAddress(warehouse.getAddress());
        express.setSenderMobile(warehouse.getSenderMobile());
        express.setSenderName(warehouse.getSenderName());
        express.setSenderRegionId(warehouse.getRegionId());
        return createExpress(shopId,express,user);
    }


    @Transactional
    public ExpressDto searchExpressByDBExpress(Express express,UserDto user){
        ExpressDto expressDto=cloneObj(express, ExpressDto.class);
        //判断运单是否最终状态，不是则需调第三方API查询
        Byte status= express.getStatus();
        if (!(status==Express.SIGN_FOR||status==Express.CANCEL||status==Express.MISS||status==Express.RECYCLE||status==Express.BROKEN)){
            ShopLogistics shopLogistics=express.getShopLogistics();
            if (ShopLogistics.INVALID == shopLogistics.getInvalid()) {
                throw new BusinessException(ReturnNo.FREIGHT_SHOPLOGISTICS_INVALID, String.format(ReturnNo.FREIGHT_SHOPLOGISTICS_INVALID.getMessage(), shopLogistics.getId()));
            }
            //找到对应渠道bean对象
            ExpressAdaptor expressAdaptor=this.expressAdaptorFactory.createExpressAdaptor(shopLogistics);
            //根据运单号查询物流
            GetExpressAdaptorDto getExpressAdaptorDto= expressAdaptor.returnExpressByBillCode(express);
            //更新此次查询结果到数据库
            express.setStatus(getExpressAdaptorDto.getStatus());
            expressDao.saveById(express,user);
            //改变运单状态
            expressDto.setStatus(getExpressAdaptorDto.getStatus());
            //因为modify信息变了，重新查出来
            express=expressDao.findById(express.getId());
//        expressDto.setRoutes(getExpressAdaptorDto.getRoutes());
        }
        SimpleUserDto creator= SimpleUserDto.builder().id(express.getCreatorId()).userName(express.getCreatorName()).build();
        SimpleUserDto modifier=SimpleUserDto.builder().id(express.getModifierId()).userName(express.getModifierName()).build();
        expressDto.setCreator(creator);
        expressDto.setModifier(modifier);
        SimpleLogisticsDto simpleLogisticsDto=cloneObj(express.getShopLogistics().getLogistics(), SimpleLogisticsDto.class);
        expressDto.setLogistics(simpleLogisticsDto);
        ConsigneeDto skipper=ConsigneeDto.builder().name(express.getSenderName()).address(express.getSenderAddress()).mobile(express.getSenderMobile()).regionId(express.getSenderRegionId()).build();
        ConsigneeDto receiver=ConsigneeDto.builder().name(express.getDeliverName()).address(express.getDeliverAddress()).mobile(express.getDeliverMobile()).regionId(express.getDeliverRegionId()).build();
        expressDto.setShipper(skipper);
        expressDto.setReceiver(receiver);
        return expressDto;
    }

}
