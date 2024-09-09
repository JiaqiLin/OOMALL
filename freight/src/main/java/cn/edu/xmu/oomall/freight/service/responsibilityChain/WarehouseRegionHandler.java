package cn.edu.xmu.oomall.freight.service.responsibilityChain;

import cn.edu.xmu.oomall.freight.dao.bo.Region;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;
@Repository
public class WarehouseRegionHandler extends Handler{
    private static final Logger logger = LoggerFactory.getLogger(WarehouseRegionHandler.class);

    @Override
    public List<Warehouse> handle(List<Warehouse> warehouses, Long regionId, Long shopId) {
        //筛选掉不负责该地区的仓库
        warehouses=warehouses.stream().filter(warehouse -> warehouse.ifContainRegion(regionId)).collect(Collectors.toList());
        System.out.println("筛选掉不负责该地区的仓库");
        for(Warehouse warehouse : warehouses){
            System.out.println(warehouse);
        }
        return this.next.handle(warehouses,regionId,shopId);
    }
}
