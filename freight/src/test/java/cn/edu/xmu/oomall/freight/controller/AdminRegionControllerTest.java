package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.service.WarehouseRegionService;
import cn.edu.xmu.oomall.freight.service.WarehouseService;
import cn.edu.xmu.oomall.freight.service.dto.WarehouseRegionDto;
import cn.edu.xmu.oomall.freight.service.dto.WarehousesDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(classes = FreightApplication.class)
@AutoConfigureMockMvc
@Transactional
public class AdminRegionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedisUtil redisUtil;

    private static String adminToken;

    @MockBean(name = "cn.edu.xmu.oomall.freight.service.WarehouseService")
    private WarehouseService warehouseService;

    @MockBean(name="cn.edu.xmu.oomall.freight.service.WarehouseRegionService")
    private WarehouseRegionService warehouseRegionService;

    JwtHelper jwtHelper = new JwtHelper();

    @BeforeAll
    public static void setup(){
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
    }

    @Test
    public void retrieveWarehouse() throws Exception{
       Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        WarehousesDto warehousesDto=new WarehousesDto();
        List<WarehousesDto> list=new ArrayList<>();
        list.add(warehousesDto);
        PageDto<WarehousesDto> dto=new PageDto<>(list,0,1,10,0);
        Mockito.when(warehouseService.retrieveWarehouse(1L, 1043L,1, 10, new UserDto())).thenReturn(dto);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/{shopId}/regions/{id}/warehouses",1L,1043L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.page", is(1)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void delWarehouseRegion() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/shops/{shopId}/warehouses/{wid}/regions/{id}", 1L, 1L,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void retrieveWarehouseRegion() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        WarehouseRegionDto warehouseRegionDto=new WarehouseRegionDto();
        List<WarehouseRegionDto> list=new ArrayList<>();
        list.add(warehouseRegionDto);
        PageDto<WarehouseRegionDto> dto=new PageDto<>(list,0,1,10,0);
        Mockito.when(warehouseRegionService.retrieveWarehouseRegion(1L,1L,1,10,new UserDto())).thenReturn(dto);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/{shopId}/warehouses/{id}/regions",1L,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.page", is(1)))
                .andDo(MockMvcResultHandlers.print());
    }
}
