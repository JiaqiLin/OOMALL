package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.controller.vo.WarehouseRegionUpdateVo;
import cn.edu.xmu.oomall.freight.controller.vo.WarehouseRegionVo;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseRegion;
import cn.edu.xmu.oomall.freight.service.dto.WarehouseRegionDto;
import cn.edu.xmu.oomall.freight.service.dto.WarehousesDto;
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

@SpringBootTest(classes= FreightApplication.class)
@Transactional
public class WarehouseRegionServiceTest {

    @Autowired
    private WarehouseRegionService warehouseRegionService;

    @MockBean
    private RedisUtil redisUtil;

    //重复出997错误
    @Test
    public void createWarehouseRegionTest1(){
        WarehouseRegionUpdateVo warehouseRegionVo=new WarehouseRegionUpdateVo();
        warehouseRegionVo.setBeginTime("2022-01-01T12:12:12");
        warehouseRegionVo.setEndTime("2023-01-01T12:12:12");
        UserDto user=new UserDto();
        user.setId(Long.valueOf("1"));
        user.setName("test1");
        //WarehouseRegionDto dto=warehouseRegionService.createWarehouseRegion(1L,1L,1L,beginTime,endTime,user);
        assertThrows(BusinessException.class,()->warehouseRegionService.createWarehouseRegion(1L,1L,1L,warehouseRegionVo,user));
        //assertEquals("test1",dto.getCreator().getUserName());
        //assertEquals(1L,dto.getCreator().getId());
    }

    //地区不存在
    @Test
    public void createWarehouseRegionTest2(){
        WarehouseRegionUpdateVo warehouseRegionVo=new WarehouseRegionUpdateVo();
        warehouseRegionVo.setBeginTime("2022-01-01T12:12:12");
        warehouseRegionVo.setEndTime("2023-01-01T12:12:12");
        UserDto user=new UserDto();
        user.setId(Long.valueOf("1"));
        user.setName("test1");
        assertThrows(BusinessException.class,()->warehouseRegionService.createWarehouseRegion(1L,1L,1111111L,warehouseRegionVo,user));
    }

    //仓库不存在
    @Test
    public void createWarehouseRegionTest3(){
        WarehouseRegionUpdateVo warehouseRegionVo=new WarehouseRegionUpdateVo();
        warehouseRegionVo.setBeginTime("2022-01-01T12:12:12");
        warehouseRegionVo.setEndTime("2023-01-01T12:12:12");
        UserDto user=new UserDto();
        user.setId(Long.valueOf("1"));
        user.setName("test1");
        assertThrows(BusinessException.class,()->warehouseRegionService.createWarehouseRegion(1L,26L,1L,warehouseRegionVo,user));
    }

    @Test
    public void createWarehouseRegionTest4(){
        WarehouseRegionUpdateVo warehouseRegionVo=new WarehouseRegionUpdateVo();
        warehouseRegionVo.setBeginTime("2022-01-01T12:12:12");
        warehouseRegionVo.setEndTime("2023-01-01T12:12:12");
        UserDto user=new UserDto();
        user.setId(Long.valueOf("1"));
        user.setName("test1");
        WarehouseRegionDto dto=warehouseRegionService.createWarehouseRegion(1L,9L,1L,warehouseRegionVo,user);
        assertEquals("test1",dto.getCreator().getUserName());
        assertEquals(1L,dto.getCreator().getId());
    }

    @Test
    public void updateWarehouseRegion(){
        WarehouseRegionUpdateVo warehouseRegionVo=new WarehouseRegionUpdateVo();
        warehouseRegionVo.setBeginTime("2022-01-01T12:12:12");
        warehouseRegionVo.setEndTime("2023-01-01T12:12:12");

        UserDto user = new UserDto();
        user.setId(Long.valueOf("1"));
        user.setName("test1");

        WarehouseRegionDto ret=warehouseRegionService.updateWarehouseRegion(1L,1L,1L,warehouseRegionVo,user);
        assertEquals("test1", ret.getModifier().getUserName());
        assertEquals(1L,ret.getModifier().getId());
    }

    @Test
    public void delWarehouseRegion(){
        ReturnObject object=warehouseRegionService.delWarehouseRegion(1L,1L,1L,new UserDto());
        assertEquals(ReturnNo.OK.getErrNo(),object.getErrno());
    }

    @Test
    public void retrieveWarehouseRegion(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        PageDto<WarehouseRegionDto> ret=warehouseRegionService.retrieveWarehouseRegion(2L,1L,1,10,new UserDto());
        assertEquals(1L,ret.getList().get(0).getRegion().getId());
    }
}
