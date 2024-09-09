package cn.edu.xmu.oomall.freight.service.responsibilityChain;

import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class WarehousePriorityHandler extends Handler{

    private static final Logger logger = LoggerFactory.getLogger(WarehousePriorityHandler.class);
    @Override
    public List<Warehouse> handle(List<Warehouse> warehouses, Long regionId, Long shopId) {
        //按仓库优先级排序
        warehouses= warehouses.stream().sorted(Comparator.comparing(Warehouse::getPriority)).collect(Collectors.toList());
        System.out.println("按仓库优先级排序");
        for(Warehouse warehouse : warehouses){
            System.out.println(warehouse);
        }
        return this.next.handle(warehouses,regionId,shopId);
    }
}
