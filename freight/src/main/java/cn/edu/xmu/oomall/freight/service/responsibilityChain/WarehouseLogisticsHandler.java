package cn.edu.xmu.oomall.freight.service.responsibilityChain;

import cn.edu.xmu.oomall.freight.dao.LogisticsDao;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;
@Repository
public class WarehouseLogisticsHandler extends Handler{

    private static final Logger logger = LoggerFactory.getLogger(WarehouseLogisticsHandler.class);
    private ShopLogisticsDao shopLogisticsDao;

    @Autowired
    public WarehouseLogisticsHandler(ShopLogisticsDao shopLogisticsDao){
        this.shopLogisticsDao=shopLogisticsDao;
    }
    @Override
    public List<Warehouse> handle(List<Warehouse> warehouses, Long regionId, Long shopId) {
        //获得可用物流
        List<ShopLogistics> shopLogisticsList= shopLogisticsDao.retrieveByShopId(shopId,1,10).stream().filter(shopLogistics -> !shopLogistics.ifUndeliverableRegion(regionId)).filter(shopLogistics -> shopLogistics.getInvalid()==ShopLogistics.VALID).collect(Collectors.toList());
        System.out.println("获得可用物流");
        for(ShopLogistics shopLogistics : shopLogisticsList){
            System.out.println(shopLogistics);
        }
        //使用可用物流筛选各仓库的物流,并将已无物流的仓库筛掉
        warehouses=warehouses.stream().map(warehouse -> {warehouse.filterWarehouseLogistics(shopLogisticsList);return warehouse;})
                           .filter(warehouse -> warehouse.getWarehouseLogisticsList().size()>0)
                           .collect(Collectors.toList());
        System.out.println("使用可用物流筛选各仓库的物流,并将已无物流的仓库筛掉");
        for(Warehouse warehouse : warehouses){
            System.out.println(warehouse);
        }
        return this.next.handle(warehouses,regionId,shopId);

    }
}
