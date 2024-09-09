package cn.edu.xmu.oomall.freight.service.responsibilityChain;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.oomall.freight.dao.ExpressDao;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Repository
public class ResultHandler extends Handler{

    private static final Logger logger = LoggerFactory.getLogger(ResultHandler.class);
    @Override
    public List<Warehouse> handle(List<Warehouse> warehouses, Long regionId, Long shopId) {
        if(warehouses.size()==1&&warehouses.get(0).getWarehouseLogisticsList().size()==1){
            System.out.println("结果");
            System.out.println(warehouses.get(0));

        }
        else{
            throw new BusinessException(ReturnNo.FREIGHT_BESTMATCH_FAIL);
        }
        return warehouses;
    }
}
