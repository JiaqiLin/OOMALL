package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.dao.LogisticsDao;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class LogisticsService {
    private LogisticsDao logisticsDao;
    @Autowired
    public LogisticsService(LogisticsDao LogisticsDao){
        this.logisticsDao=LogisticsDao;
    }

    public ReturnObject retrieveWarehouses(String billCode,UserDto userDto){
        return null;
    }

    public Logistics findLogisticsByBillCode(String billCode) {
        List<Logistics> list = logisticsDao.retrieveAll().stream().filter(logistics -> logistics.isMatch(billCode)).collect(Collectors.toList());
        if(list.isEmpty())
            throw new BusinessException(ReturnNo.FREIGHT_BILLCODE_NOTEXIST);
        else
            return list.get(0);
    }
}
