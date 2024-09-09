package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.Undeliverable;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseLogistics;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
public class WarehouseLogisticsDaoTest {
    @Autowired
    private WarehouseLogisticsDao warehouseLogisticsDao;

    @Test
    public void findByIdTest1(){
        WarehouseLogistics warehouseLogistics = new WarehouseLogistics();
        warehouseLogistics.setId(Long.valueOf(797));
        warehouseLogistics.setWarehouseId(Long.valueOf(25));
        warehouseLogistics.setShopLogisticsId(Long.valueOf(1));
        warehouseLogistics.setInvalid(WarehouseLogistics.VALID);


        WarehouseLogistics ret = warehouseLogisticsDao.findById(Long.valueOf(797));

        assertThat(ret.getId()).isEqualTo(warehouseLogistics.getId());
        assertThat(ret.getWarehouseId()).isEqualTo(warehouseLogistics.getWarehouseId());
        assertThat(ret.getShopLogisticsId()).isEqualTo(warehouseLogistics.getShopLogisticsId());
        assertThat(ret.getInvalid()).isEqualTo(warehouseLogistics.getInvalid());
    }


    @Test
    public void findByIdTest2(){
        WarehouseLogistics warehouseLogistics = new WarehouseLogistics();
        warehouseLogistics.setId(Long.valueOf(797000));
        warehouseLogistics.setWarehouseId(Long.valueOf(25));
        warehouseLogistics.setShopLogisticsId(Long.valueOf(1));
        warehouseLogistics.setInvalid(WarehouseLogistics.VALID);


        assertThrows(BusinessException.class,()->warehouseLogisticsDao.findById(Long.valueOf(797000)));
    }

    @Test
    public void save(){
        UserDto user = new UserDto();
        user.setName("10086");
        user.setUserLevel(10086);
        WarehouseLogistics obj = new WarehouseLogistics();
        obj.setId(Long.valueOf(10086));
        obj.setInvalid(WarehouseLogistics.VALID);
        obj.setWarehouseId((long)10086);
        obj.setShopLogisticsId((long)10086);
        LocalDateTime begin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse("2032-01-01T12:12:12", DATE_TIME_FORMATTER);
        obj.setBeginTime(begin);
        obj.setEndTime(end);
        System.out.println(warehouseLogisticsDao.save(obj, user).getErrno() == ReturnNo.OK.getErrNo());

    }

    @Test
    public void saveByIdTest(){
        WarehouseLogistics obj = new WarehouseLogistics();
        obj.setShopLogisticsId(1L);
        UserDto user = new UserDto();
        user.setName("test1");
        user.setUserLevel(1);

        warehouseLogisticsDao.saveById(obj,user);

        WarehouseLogistics ret = warehouseLogisticsDao.findById(797L);

        assertThat(ret.getShopLogisticsId()).isEqualTo(1L);

    }

    @Test
    public void findByLidAndWid() {
        WarehouseLogistics warehouseLogistics = new WarehouseLogistics();
        warehouseLogistics.setId(Long.valueOf(797));
        warehouseLogistics.setWarehouseId(Long.valueOf(25));
        warehouseLogistics.setShopLogisticsId(Long.valueOf(1));
        warehouseLogistics.setInvalid(WarehouseLogistics.VALID);


        WarehouseLogistics ret = warehouseLogisticsDao.findByLidAndWid(Long.valueOf(25), Long.valueOf(1));

        assertThat(ret.getId()).isEqualTo(warehouseLogistics.getId());
        assertThat(ret.getWarehouseId()).isEqualTo(warehouseLogistics.getWarehouseId());
        assertThat(ret.getShopLogisticsId()).isEqualTo(warehouseLogistics.getShopLogisticsId());
        assertThat(ret.getInvalid()).isEqualTo(warehouseLogistics.getInvalid());
    }

    @Test
    public void retrieveByWarehouseId(){
        PageInfo<WarehouseLogistics> warehouseLogistics=warehouseLogisticsDao.retrieveByWarehouseId(25L,1,10);
        assertThat(warehouseLogistics.getList().get(0).getId()).isEqualTo(797L);
        assertThat(warehouseLogistics.getList().get(0).getShopLogisticsId()).isEqualTo(1L);
    }

    @Test
    public void delByIdTest(){
        WarehouseLogistics warehouseLogistics = warehouseLogisticsDao.findById(797L);
        warehouseLogisticsDao.delById(warehouseLogistics);
        assertThrows(BusinessException.class,()->warehouseLogisticsDao.findById(797L)) ;
    }

}

