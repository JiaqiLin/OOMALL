package cn.edu.xmu.oomall.freight.service.responsibilityChain;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseLogistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Repository
public class ShopLogisticsPriorityHandler extends Handler{

    private static final Logger logger = LoggerFactory.getLogger(ShopLogisticsPriorityHandler.class);
    @Override
    public List<Warehouse> handle(List<Warehouse> warehouses, Long regionId, Long shopId) {
        //先获取所有仓库中所有商户物流，将商户物流拼成List，按优先级排序，最后根据商户物流id去重，得到按优先级排好的商户物流
        List<ShopLogistics> shopLogisticsList=warehouses.stream().map(warehouse -> warehouse.getWarehouseLogisticsList().stream().map(warehouseLogistics -> warehouseLogistics.getShopLogistics()).collect(Collectors.toList())).flatMap(Collection::stream).sorted(Comparator.comparing(ShopLogistics::getPriority)).collect(collectingAndThen(
                toCollection(() -> new TreeSet<>(Comparator.comparing(ShopLogistics::getId))), ArrayList::new));

        System.out.println("先获取所有仓库中所有商户物流，将商户物流拼成List，按优先级排序，最后根据商户物流id去重，得到按优先级排好的商户物流");
        for(ShopLogistics shopLogistics : shopLogisticsList){
            System.out.println(shopLogistics);
        }
        if(shopLogisticsList.size()==0){
            throw new BusinessException(ReturnNo.FREIGHT_BESTMATCH_FAIL);
        }
        //使用优先级最高的商户物流
        Long shopLogisticsId=shopLogisticsList.get(0).getId();
        System.out.println("使用优先级最高的商户物流");
        System.out.println(shopLogisticsId);

        //用该商户物流去筛选仓库，仓库必须拥有该物流，且最后只能使用该物流
        // 因此将除这个物流外的其他物流筛掉，并将没有这个物流的仓库筛掉
        warehouses = warehouses.stream()
                .map(warehouse -> {warehouse.setWarehouseLogisticsList(warehouse.getWarehouseLogisticsList()
                                                                        .stream().filter(warehouseLogistics -> warehouseLogistics.getShopLogisticsId().equals(shopLogisticsId))
                                                                        .collect(Collectors.toList()));return warehouse;})
                .filter(warehouse -> warehouse.getWarehouseLogisticsList().size()>0).collect(Collectors.toList());
        System.out.println("将除这个物流外的其他物流筛掉，并将没有这个物流的仓库筛掉");
        for(Warehouse warehouse : warehouses){
            System.out.println(warehouse);
        }

        return this.next.handle(warehouses,regionId,shopId);
    }
}
