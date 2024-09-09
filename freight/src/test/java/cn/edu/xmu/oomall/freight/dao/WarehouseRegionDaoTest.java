package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseRegion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static cn.edu.xmu.javaee.core.model.Constants.DATE_TIME_FORMATTER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class WarehouseRegionDaoTest {
    @Autowired
    private WarehouseRegionDao warehouseRegionDao;

    @Test
    public void findByIdTest1(){
        WarehouseRegion ret=warehouseRegionDao.findById(1L);
        assertThat(ret.getWarehouseId()).isEqualTo(1L);
        assertThat(ret.getRegionId()).isEqualTo(1L);
    }

    //查找不存在数据
    @Test
    public void findByIdTest2(){
        assertThrows(BusinessException.class,()->warehouseRegionDao.findById(26L));
    }

    @Test
    public void saveTest(){
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        WarehouseRegion obj=new WarehouseRegion();
        obj.setWarehouseId(26L);
        obj.setRegionId(26L);
        LocalDateTime begin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse("2023-01-01T12:12:12", DATE_TIME_FORMATTER);
        obj.setBeginTime(begin);
        obj.setEndTime(end);
        warehouseRegionDao.save(obj,user);
        WarehouseRegion warehouseRegion=warehouseRegionDao.findById(obj.getId());
        System.out.println(warehouseRegion);
    }

    @Test
    public void findByRegionIdAndWarehouseIdTest1(){
        WarehouseRegion warehouseRegion=warehouseRegionDao.findByRegionIdAndWarehouseId(7362L,7L);
        assertThat(warehouseRegion.getId()).isEqualTo(7L);
    }

    @Test
    public void saveById(){
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        WarehouseRegion obj=new WarehouseRegion();
        obj.setWarehouseId(26L);
        obj.setRegionId(26L);
        LocalDateTime begin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse("2023-01-01T12:12:12", DATE_TIME_FORMATTER);
        obj.setBeginTime(begin);
        obj.setEndTime(end);
        warehouseRegionDao.saveById(obj,user);
        WarehouseRegion warehouseRegion=warehouseRegionDao.findById(obj.getId());
        System.out.println(warehouseRegion);
    }

    @Test
    public void retrieveByWarehouseId(){
        List<WarehouseRegion> ret=warehouseRegionDao.retrieveByWarehouseId(1L,1,10);
        assertThat(ret.get(0).getId()).isEqualTo(1L);
        assertThat(ret.get(0).getRegionId()).isEqualTo(1L);
    }
}
