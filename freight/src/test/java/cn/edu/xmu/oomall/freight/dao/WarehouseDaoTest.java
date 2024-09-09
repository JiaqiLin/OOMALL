package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseLogistics;
import com.github.pagehelper.PageInfo;
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
public class WarehouseDaoTest {
    @Autowired
    private WarehouseDao warehouseDao;

    /**
     * 查找存在的warehouse
     */
    @Test
    public void findByIdTest1(){
        Warehouse warehouse = new Warehouse();
        warehouse.setAddress("北京,朝阳,东坝,朝阳新城第二曙光路14号");
        warehouse.setId(Long.valueOf(1));
        warehouse.setShopId(Long.valueOf(1));
        warehouse.setName("朝阳新城第二仓库");
        warehouse.setSenderName("阮杰");
        warehouse.setRegionId(Long.valueOf(1043));
        warehouse.setSenderMobile("139542562579");
        warehouse.setPriority(Integer.valueOf(1000));
        warehouse.setInvalid(Warehouse.VALID);

        Warehouse ret = warehouseDao.findById(Long.valueOf(1));

        assertThat(ret.getAddress()).isEqualTo(warehouse.getAddress());
        assertThat(ret.getId()).isEqualTo(warehouse.getId());
        assertThat(ret.getShopId()).isEqualTo(warehouse.getShopId());
        assertThat(ret.getName()).isEqualTo(warehouse.getName());
        assertThat(ret.getSenderName()).isEqualTo(warehouse.getSenderName());
        assertThat(ret.getRegionId()).isEqualTo(warehouse.getRegionId());
        assertThat(ret.getSenderMobile()).isEqualTo(warehouse.getSenderMobile());
        assertThat(ret.getPriority()).isEqualTo(warehouse.getPriority());
        assertThat(ret.getInvalid()).isEqualTo(warehouse.getInvalid());
    }

//    /**
//     * 查找不存在的warehouse
//     */
//    @Test
//    public void findByIdTest2(){
//        assertThrows(BusinessException.class, () -> warehouseDao.findById(Long.valueOf(26)));
//    }

    @Test
    public void findByIdTest3(){
        Warehouse warehouse=warehouseDao.findById(9L);
        assertThat(warehouse.getShopId()).isEqualTo(3L);
        System.out.println(warehouse);
    }

    @Test
    public void save(){
        UserDto user = new UserDto();
        user.setName("test1");
        user.setUserLevel(1);
        Warehouse obj = new Warehouse();
        obj.setAddress("testAddress");
        obj.setId(Long.valueOf(1));
        obj.setShopId(Long.valueOf(2));
        obj.setName("testName");
        obj.setSenderName("testSenderName");
        obj.setRegionId(Long.valueOf(100));
        obj.setSenderMobile("11223334444");
        obj.setPriority(Integer.valueOf(1000));
        obj.setInvalid(Warehouse.VALID);
        LocalDateTime begin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
        obj.setBeginTime(begin);
        warehouseDao.save(obj, user);
        Warehouse warehouse = warehouseDao.findById(obj.getId());
        System.out.println(warehouse);
    }

    /**
     * 查找所有
     */
    @Test
    public void retrieveAllTest(){
        List<Warehouse> ret = warehouseDao.retrieveAll();
        System.out.println(ret);
    }

    @Test
    public void retrieveOrderByPriorityTest(){
        List<Warehouse> ret = warehouseDao.retrieveOrderByPriority();
        for(Warehouse w : ret){
            System.out.println(w.getId() + " " + w.getPriority());
        }
    }

    @Test
    public void findByRegionIdTest(){
        Warehouse warehouse=warehouseDao.findByRegionId(1043L);
        assertThat(warehouse.getName()).isEqualTo("朝阳新城第二仓库");
        assertThat(warehouse.getShopId()).isEqualTo(1L);
        assertThat(warehouse.getAddress()).isEqualTo("北京,朝阳,东坝,朝阳新城第二曙光路14号");
    }

    @Test void retrieveByRegionIdTest(){
        List<Warehouse> warehouse=warehouseDao.retrieveByRegionId(1043L,1,10);
        assertThat(warehouse.get(0).getShopId()).isEqualTo(1L);
        assertThat(warehouse.get(0).getId()).isEqualTo(1L);
    }
}
