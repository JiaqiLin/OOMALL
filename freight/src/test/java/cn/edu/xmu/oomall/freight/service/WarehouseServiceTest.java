package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.controller.vo.WarehouseVo;
import cn.edu.xmu.oomall.freight.dao.WarehouseDao;
import cn.edu.xmu.oomall.freight.service.dto.WareHouseDto;
import cn.edu.xmu.oomall.freight.service.dto.WarehousesDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class WarehouseServiceTest {
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private WarehouseDao warehouseDao;
    @MockBean
    private RedisUtil redisUtil;

    @Test
    public void createWarehouseTest01() {
        WarehouseVo warehouseVo = new WarehouseVo();
        warehouseVo.setName("New York Warehouse");
        warehouseVo.setAddress("New York, United States");
        warehouseVo.setRegionId((long)1);
        warehouseVo.setSenderName("Eric Lannister");
        warehouseVo.setSenderMobile("123123124124");

        UserDto user = new UserDto();
        user.setName("test1");
        user.setId(Long.valueOf("1"));

        WareHouseDto dto = warehouseService.createWarehouses((long) 1, user, warehouseVo);

        assertEquals("New York Warehouse",dto.getName());
        assertEquals("Eric Lannister",dto.getSenderName());
        assertEquals("123123124124",dto.getSenderMobile());
        assertEquals("New York, United States",dto.getAddress());
        assertEquals(1L,dto.getRegion().getId());
    }

    @Test
    public void retrieveShopLogisticsTest01(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        UserDto user = new UserDto();
        user.setId(Long.valueOf("1"));
        user.setName("test1");

        PageDto<WareHouseDto> dto = warehouseService.retrieveWarehouses(1L, 1, 1, user);
        System.out.println("dto=====================\t"+dto.getList().get(0));
        assertEquals(1043L, dto.getList().get(0).getRegion().getId());
    }


    @Test
    public void updateWarehouseTest01() {
        WarehouseVo warehouseVo = new WarehouseVo();
        warehouseVo.setPriority(1000);
        warehouseVo.setName("new-name");
        warehouseVo.setRegionId(1043L);

        UserDto user = new UserDto();
        user.setId(Long.valueOf("1"));
        user.setName("test1");

        WareHouseDto ret = warehouseService.updateWarehouses(1L, 1L, user, warehouseVo);

        assertEquals("test1", ret.getModifier().getName());
        assertEquals(1000, ret.getPriority());
        assertEquals("new-name", ret.getName());
    }

    @Test
    public void delWarehouseTest01() {
        ReturnObject returnObject = warehouseService.delWarehouses(1L, 1L);
        assertEquals(ReturnNo.OK.getErrNo(),returnObject.getErrno());
    }

    @Test
    public void retrieveWarehouseTest(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        PageDto<WarehousesDto> warehouses=warehouseService.retrieveWarehouse(2L,1068L,1,10,new UserDto());
        assertEquals(2L,warehouses.getList().get(0).getWarehouse().getId());
    }
}
