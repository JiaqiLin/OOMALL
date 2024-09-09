package cn.edu.xmu.oomall.freight.service.responsibilityChain;

import cn.edu.xmu.oomall.freight.dao.WarehouseDao;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class WarehouseHandler extends Handler{

    private static final Logger logger = LoggerFactory.getLogger(WarehouseHandler.class);
    private WarehouseDao warehouseDao;

    @Autowired
    public WarehouseHandler(WarehouseDao warehouseDao){
        this.warehouseDao=warehouseDao;
    }


    @Override
    public List<Warehouse> handle(List<Warehouse> warehouses, Long regionId, Long shopId) {
       //获取该商户的所有有效仓库
       warehouses= warehouseDao.retrieveByShopId(shopId,1,10).getList().stream().filter(warehouse -> warehouse.getInvalid()==Warehouse.VALID).collect(Collectors.toList());
       System.out.println("获取该商户的所有有效仓库");
       for(Warehouse warehouse : warehouses){
           System.out.println(warehouse);
       }
        return this.next.handle(warehouses,regionId,shopId);
    }
}
