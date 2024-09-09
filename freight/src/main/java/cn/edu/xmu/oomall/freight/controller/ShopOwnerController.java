package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.controller.vo.*;
import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.service.*;
import cn.edu.xmu.oomall.freight.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(produces = "application/json;charset=UTF-8")
public class ShopOwnerController {
    private final Logger logger = LoggerFactory.getLogger(ShopOwnerController.class);
    private WarehouseService warehouseService;

    private ShopLogisticsService shopLogisticsService;

    private UndeliverableService undeliverableService;

    private WarehouseRegionService warehouseRegionService;

    private LogisticsService logisticsService;

    private WarehouseLogisticsService warehouseLogisticsService;


    @Autowired
    public ShopOwnerController(WarehouseService warehouseService, ShopLogisticsService shopLogisticsService, UndeliverableService undeliverableService, WarehouseRegionService warehouseRegionService, LogisticsService logisticsService, WarehouseLogisticsService warehouseLogisticsService) {

        this.warehouseService = warehouseService;
        this.shopLogisticsService = shopLogisticsService;
        this.warehouseRegionService=warehouseRegionService;
        this.undeliverableService=undeliverableService;
        this.logisticsService=logisticsService;

        this.warehouseLogisticsService = warehouseLogisticsService;
    }

    /**
     * 根据物流单号查询属于哪家快递公司
     * @param billCode
     * @param userDto
     * @return
     */
    @GetMapping("/logistics")
    public ReturnObject findLogisticsByBillCode(@RequestParam String billCode,
                                           @LoginUser UserDto userDto){
        logger.debug("retrieveWarehouses: billCode = {}", billCode);
        Logistics logistics = logisticsService.findLogisticsByBillCode(billCode);
        LogisticsDto dto=LogisticsDto.builder().name(logistics.getName()).id(logistics.getId()).build();
        return new ReturnObject(ReturnNo.OK, dto);
    }


    /***
     * @description: 根据shopId和页码页数获得当前商铺下的仓库信息列表
     * @param:
     * @return: cn.edu.xmu.javaee.core.model.ReturnObject
     * @author Mr.X
     * @date: 12:43 2022/12/4
     */

    @GetMapping("/shops/{shopId}/warehouses")
    public ReturnObject retrieveWarehouses(@PathVariable Long shopId,
                                           @RequestParam(required = false,defaultValue = "1")Integer page,
                                           @RequestParam(required = false,defaultValue = "10")Integer pageSize,
                                           @LoginUser UserDto userDto){
        PageDto<WareHouseDto> dtoPageDto = warehouseService.retrieveWarehouses(shopId, page, pageSize, userDto);
        return new ReturnObject(dtoPageDto);
    }

    @PutMapping("/shops/{shopId}/warehouses/{id}")
    public ReturnObject updateWarehouses(@PathVariable("shopId") Long shopId,
                                         @PathVariable("id") Long id,
                                         @Validated @RequestBody WarehouseVo warehouseVo,
                                         @LoginUser UserDto userDto){
        return warehouseService.updateWarehouses(shopId, id, userDto, warehouseVo) == null ?
                new ReturnObject(ReturnNo.AUTH_NO_RIGHT):new ReturnObject(ReturnNo.OK);
    }

    @DeleteMapping("/shops/{shopId}/warehouses/{id}")
    public ReturnObject delWarehouses(@PathVariable("shopId") Long shopId,
                                         @PathVariable("id") Long id,
                                         @LoginUser UserDto userDto){
        ReturnObject returnObject = warehouseService.delWarehouses(shopId, id);
        return returnObject;
    }

    /*
    * 商户指定快递公司无法配送某个地区
    * */

    @PostMapping("/shops/{shopId}/shoplogistics/{id}/regions/{rid}/undeliverable")
    public ReturnObject createUndeliverable(@PathVariable("shopId")Long shopId,
                                            @PathVariable("rid")Long regionId,
                                            @PathVariable("id")Long shopLogisticsId,
                                            @Validated@RequestBody UndeliverableVo undeliverableVo,
                                            @LoginUser UserDto userDto){

        UndeliverableDto undeliverableDto = undeliverableService.createUndeliverable(regionId,shopLogisticsId,undeliverableVo,userDto);
        return new ReturnObject(ReturnNo.CREATED);
    }
    /*
     * 商户更新不可达信息
     * */

    @PutMapping("/shops/{shopId}/shoplogistics/{id}/regions/{rid}/undeliverable")
    public ReturnObject updateUndeliverable(@PathVariable("shopId")Long shopId,
                                            @PathVariable("rid")Long regionId,
                                            @PathVariable("id")Long shopLogisticsId,
                                            @Validated@RequestBody UndeliverableVo undeliverableVo,
                                            @LoginUser UserDto userDto){
        UndeliverableDto undeliverableDto = undeliverableService.updateUndeliverable(regionId,shopLogisticsId,undeliverableVo,userDto);
        return new ReturnObject(ReturnNo.OK,undeliverableDto);
    }
    /*
     * 商户删除某个不可达信息
     * */

    @DeleteMapping("/shops/{shopId}/shoplogistics/{id}/regions/{rid}/undeliverable")
    public ReturnObject delUndeliverable(@PathVariable("shopId")Long shopId,
                                            @PathVariable("rid")Long regionId,
                                         @PathVariable("id")Long shopLogisticsId,
                                                     @LoginUser UserDto userDto){
        undeliverableService.delUndeliverable(regionId,shopLogisticsId,userDto);
        return new ReturnObject(ReturnNo.OK,null);
    }
    /*
     * 商户查询快递公司无法配送的地区
     * */

    @GetMapping("/shops/{shopId}/shoplogistics/{id}/undeliverableregions")
    public ReturnObject retreiveUndeliverable(@PathVariable("shopId")Long shopId,
                                            @PathVariable("id")Long shopLogisticsId,
                                              @RequestParam(required = false,defaultValue = "1")Integer page,
                                              @RequestParam(required = false,defaultValue = "10")Integer pageSize,
                                              @LoginUser UserDto userDto){
        PageDto<UndeliverableDto> dtoPageDto = undeliverableService.retrieveUndeliverableRegions(shopLogisticsId,page,pageSize,userDto);
        return new ReturnObject(ReturnNo.OK,dtoPageDto);
    }
    /**
     * 店家新增物流合作
     * @param shopId
     * @param shopLogisticsVo
     * @param userDto
     * @return
     */
    @PostMapping("/shops/{shopId}/shoplogistics")
    public ReturnObject createShopLogistics(@PathVariable("shopId") Long shopId,
                                            @Validated @RequestBody ShopLogisticsVo shopLogisticsVo,
                                            @LoginUser UserDto userDto) {

        logger.debug("createShopLogistics: shopLogisticsVo = {}", shopLogisticsVo);
        ShopLogisticsDto shopLogisticsDto = shopLogisticsService.createShopLogistics(shopId, userDto, shopLogisticsVo);
        return new ReturnObject(ReturnNo.CREATED, shopLogisticsDto);
    }

    @PostMapping("/shops/{shopId}/warehouses")
    public ReturnObject createWarehouse(@PathVariable("shopId") Long shopId,
                                        @Validated @RequestBody WarehouseVo warehouseVo,
                                        @LoginUser UserDto userDto) {

        logger.debug("createWarehouse: warehouseVo = {}", warehouseVo);
        WareHouseDto warehouseDto = warehouseService.createWarehouses(shopId, userDto, warehouseVo);
        return new ReturnObject(ReturnNo.CREATED, warehouseDto);
    }

    /**
     * 店家更新物流合作信息
     * @param shopId
     * @param id
     * @param shopLogisticsVo
     * @param userDto
     * @return
     */
    @PutMapping("/shops/{shopId}/shoplogistics/{id}")
    public ReturnObject updateShopLogistics(@PathVariable("shopId") Long shopId,
                                         @PathVariable("id") Long id,
                                         @Validated @RequestBody ShopLogisticsVo shopLogisticsVo,
                                         @LoginUser UserDto userDto){
        shopLogisticsService.updateShopLogistics(shopId, id, userDto, shopLogisticsVo);
        return new ReturnObject(ReturnNo.OK);
    }

    /**
     * 店家获得物流合作信息
     * @param shopId
     * @param page
     * @param pageSize
     * @param userDto
     * @return
     */
    @GetMapping("/shops/{shopId}/shoplogistics")
    public ReturnObject retrieveShopLogistics(@PathVariable Long shopId,
                                           @RequestParam(required = false,defaultValue = "1")Integer page,
                                           @RequestParam(required = false,defaultValue = "10")Integer pageSize,
                                           @LoginUser UserDto userDto){
        PageDto<ShopLogisticsDto> dtoPageDto = shopLogisticsService.retrieveShopLogistics(shopId, page, pageSize, userDto);
        return new ReturnObject(ReturnNo.OK, dtoPageDto);
    }

    /**
     * 商铺停用某个物流合作
     * @param shopId
     * @param id
     * @param userDto
     * @return
     */
    @PutMapping("/shops/{shopId}/shoplogistics/{id}/suspend")
    public ReturnObject suspendShopLogistics(@PathVariable("shopId") Long shopId,
                                         @PathVariable("id") Long id,
                                         @LoginUser UserDto userDto){
        shopLogisticsService.suspendShopLogistics(shopId, id, userDto);
        return new ReturnObject(ReturnNo.OK);
    }

    /**
     * 商铺恢复某个物流合作
     * @param shopId
     * @param id
     * @param userDto
     * @return
     */
    @PutMapping("/shops/{shopId}/shoplogistics/{id}/resume")
    public ReturnObject resumeShopLogistics(@PathVariable("shopId") Long shopId,
                                             @PathVariable("id") Long id,
                                             @LoginUser UserDto userDto){
        shopLogisticsService.resumeShopLogistics(shopId, id, userDto);
        return new ReturnObject(ReturnNo.OK);
    }


    /**
     *商户新增仓库配送地区
     */
    @PostMapping("/shops/{shopId}/warehouses/{wid}/regions/{id}")
    public ReturnObject createWarehouseRegion(@PathVariable Long shopId,
                                              @PathVariable Long wid,
                                              @PathVariable Long id,
                                              @Validated @RequestBody WarehouseRegionUpdateVo warehouseRegionVo,
                                              @LoginUser UserDto user){
        WarehouseRegionDto warehouseRegionDto=warehouseRegionService.createWarehouseRegion(shopId,wid,id,warehouseRegionVo,user);
        return new ReturnObject(ReturnNo.CREATED);
    }

    /**
     *商户修改仓库配送地区
     */
    @PutMapping("/shops/{shopId}/warehouses/{wid}/regions/{id}")
    public ReturnObject updateWarehouseRegion(@PathVariable Long shopId,
                                              @PathVariable Long wid,
                                              @PathVariable Long id,
                                              @Validated @RequestBody WarehouseRegionUpdateVo warehouseRegionVo,
                                              @LoginUser UserDto user){
        WarehouseRegionDto warehouseRegionDto=warehouseRegionService.updateWarehouseRegion(shopId,wid,id,warehouseRegionVo,user);
        return new ReturnObject(ReturnNo.OK);
    }

    /*
    商户新建仓库物流
     */
    @PostMapping("/shops/{shopId}/warehouses/{id}/shoplogistics/{lid}")
    public ReturnObject createWarehouseLogistics(@LoginUser UserDto userDto,
                                            @PathVariable("shopId") Long shopId,
                                            @PathVariable("id") Long id,
                                            @PathVariable("lid") Long lid,
                                            @Validated @RequestBody WarehouseLogisticsInfoVo warehouseLogisticsInfoVo){
        WarehouseLogisticsDto ret = warehouseLogisticsService.createWarehouseLogistics(userDto, shopId, id, lid, warehouseLogisticsInfoVo);
        return new ReturnObject(ReturnNo.CREATED,ret);

    }

    /*
    商户修改仓库物流信息
     */
    @PutMapping("/shops/{shopId}/warehouses/{id}/shoplogistics/{lid}")
    public ReturnObject updateWarehouseLogistics(@LoginUser UserDto userDto,
                                            @PathVariable("shopId") Long shopId,
                                            @PathVariable("id") Long id,
                                            @PathVariable("lid") Long lid,
                                            @Validated @RequestBody WarehouseLogisticsInfoVo warehouseLogisticsInfoVo){

        return new ReturnObject(warehouseLogisticsService.updateWarehouseLogistics(userDto,shopId,id,lid,warehouseLogisticsInfoVo));
    }

    /*
    商户删除仓库物流配送关系
     */
    @DeleteMapping("/shops/{shopId}/warehouses/{id}/shoplogistics/{lid}")
    public ReturnObject delWarehouseLogistics(@LoginUser UserDto userDto,
                                         @PathVariable("shopId") Long shopId,
                                         @PathVariable("id") Long id,
                                         @PathVariable("lid") Long lid){
        return new ReturnObject(ReturnNo.OK,warehouseLogisticsService.delWarehouseLogistics(userDto,shopId,id,lid));
    }
    /*
    获得仓库物流
     */
    @GetMapping("/shops/{shopId}/warehouses/{id}/shoplogistics")
    public ReturnObject retrieveWarehouseLogistics(@PathVariable Long shopId,
                                              @RequestParam(required = false,defaultValue = "1")Integer page,
                                              @RequestParam(required = false,defaultValue = "10")Integer pageSize,
                                              @LoginUser UserDto userDto,
                                              @PathVariable("id") Long id){
        return new ReturnObject(ReturnNo.OK,warehouseLogisticsService.retrieveWarehouseLogistics(shopId,page,pageSize,userDto,id));

    }
}
