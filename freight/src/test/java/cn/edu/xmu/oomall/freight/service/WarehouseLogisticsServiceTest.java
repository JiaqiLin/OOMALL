package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.controller.vo.WarehouseLogisticsInfoVo;
import cn.edu.xmu.oomall.freight.controller.vo.WarehouseLogisticsVo;
import cn.edu.xmu.oomall.freight.dao.WarehouseLogisticsDao;
import cn.edu.xmu.oomall.freight.service.dto.WareHouseDto;
import cn.edu.xmu.oomall.freight.service.dto.WarehouseLogisticsDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static cn.edu.xmu.javaee.core.model.Constants.DATE_TIME_FORMATTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class WarehouseLogisticsServiceTest {
    @Autowired
    private WarehouseLogisticsService warehouseLogisticsService;

    @MockBean
    RedisUtil redisUtil;

    @Autowired
    private WarehouseLogisticsDao warehouseLogisticsDao;

    @Test
    public void createWarehouseLogisticsTest1() {
//        WarehouseLogisticsVo warehouseLogisticsVo = new WarehouseLogisticsVo();
//        warehouseLogisticsVo.setShopId(Long.valueOf("1"));
//        warehouseLogisticsVo.setId(Long.valueOf("1"));
//        warehouseLogisticsVo.setLid(Long.valueOf("1"));
//        LocalDateTime begin = LocalDateTime.now();
//        LocalDateTime end = LocalDateTime.now();
        WarehouseLogisticsInfoVo warehouseLogisticsInfoVo = new WarehouseLogisticsInfoVo();
        warehouseLogisticsInfoVo.setBegintime("2022-01-01T12:12:12");
        warehouseLogisticsInfoVo.setEndtime("2023-01-01T12:12:12");

        LocalDateTime begin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);

        UserDto user = new UserDto();
        user.setName("test1");
        user.setId(Long.valueOf("1"));

//        WareHouseDto dto = new WareHouseDto();

        WarehouseLogisticsDto warehouseLogisticsDto = warehouseLogisticsService.createWarehouseLogistics(user, 1L, 1L, 1L, warehouseLogisticsInfoVo);
        assertEquals(1L, warehouseLogisticsDto.getShopLogistics().getLogistics().getId());
        assertEquals(1L, warehouseLogisticsDto.getShopLogistics().getId());
    }

    @Test
    public void retrieveWarehouseLogisticsTest1() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        UserDto user = new UserDto();
        user.setId(Long.valueOf("1"));
        user.setName("test1");

        PageDto<WarehouseLogisticsDto> dto = warehouseLogisticsService.retrieveWarehouseLogistics(1L, 1, 10,user, 1L);
//        System.out.println("dto=====================\t" + dto);
//        System.out.println();
//        System.out.println("dto.first=====================\t" + dto.getList().get(0));
        assertEquals(1L, dto.getList().get(0).getShopLogistics().getLogistics().getId());
    }

    @Test
    public void updateWarehouseLogisticsTest01() {
//        WarehouseLogisticsVo warehouseLogisticsVo = new WarehouseLogisticsVo();
//        warehouseLogisticsVo.setLid(1L);
//        warehouseLogisticsVo.setId(25L);

        WarehouseLogisticsInfoVo warehouseLogisticsInfoVo = new WarehouseLogisticsInfoVo("2022-01-01T12:12:12","2023-01-01T12:12:12");
        LocalDateTime begin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("wmh");

        WarehouseLogisticsDto ret = warehouseLogisticsService.updateWarehouseLogistics(user, 1L, 25L, 1L, warehouseLogisticsInfoVo);
//        System.out.println("serviceTest.Dto================\t"+ret);
        assertEquals(begin,ret.getBeginTime());
        assertEquals("wmh", ret.getModifier().getName());
        assertEquals(3, ret.getShopLogistics().getPriority());
        assertEquals("secret1", ret.getShopLogistics().getSecret());
    }

    @Test
    public void delWarehouseLogisticsTest01() {
        UserDto user = new UserDto();
        user.setName("test1");
        user.setUserLevel(1);

        ReturnObject returnObject = warehouseLogisticsService.delWarehouseLogistics(user, 1L, 1L, 1L);
        assertEquals(ReturnNo.OK.getErrNo(),returnObject.getErrno());
    }
}

