package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.service.RegionService;
import cn.edu.xmu.oomall.freight.service.WarehouseRegionService;
import cn.edu.xmu.oomall.freight.service.WarehouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(produces = "application/json;charset=UTF-8")
public class AdminRegionController {
    private final Logger logger = LoggerFactory.getLogger(AdminRegionController.class);
    private RegionService regionService;

    private WarehouseService warehouseService;

    private WarehouseRegionService warehouseRegionService;

    @Autowired
    public AdminRegionController(RegionService regionService,WarehouseService warehouseService,WarehouseRegionService warehouseRegionService){
        this.regionService=regionService;
        this.warehouseService=warehouseService;
        this.warehouseRegionService=warehouseRegionService;
    }


    /**
     * 管理员查询某个地区可以配送的所有仓库
     */
    @GetMapping("/shops/{shopId}/regions/{id}/warehouses")
    public ReturnObject retrieveWarehouse(@PathVariable Long shopId,
                                          @PathVariable Long id,
                                          @RequestParam(required = false,defaultValue = "1") Integer page,
                                          @RequestParam(required = false,defaultValue = "10")Integer pageSize,
                                          @LoginUser UserDto user){
        return new ReturnObject(warehouseService.retrieveWarehouse(shopId,id,page,pageSize,user));
    }

    /**
     * 管理员或商户取消仓库对某个地区的配送
     */
    @DeleteMapping("/shops/{shopId}/warehouses/{wid}/regions/{id}")
    public ReturnObject delWarehouseRegion(@PathVariable Long shopId,
                                           @PathVariable Long wid,
                                           @PathVariable Long id,
                                           @LoginUser UserDto user){
        warehouseRegionService.delWarehouseRegion(shopId,wid,id,user);
        return new ReturnObject(ReturnNo.OK);
    }

    /**
     * 管理员或商户查询某个仓库的配送地区
     */
    @GetMapping("/shops/{shopId}/warehouses/{id}/regions")
    public ReturnObject retrieveWarehouseRegion(@PathVariable Long shopId,
                                                @PathVariable Long id,
                                                @RequestParam(required = false,defaultValue = "1") Integer page,
                                                @RequestParam(required = false,defaultValue = "10")Integer pageSize,
                                                @LoginUser UserDto user){
        return new ReturnObject(warehouseRegionService.retrieveWarehouseRegion(shopId, id, page, pageSize, user));
    }
}
