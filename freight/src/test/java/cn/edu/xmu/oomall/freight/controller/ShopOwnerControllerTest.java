package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.controller.vo.*;
import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.service.*;
import cn.edu.xmu.oomall.freight.service.dto.*;
import org.hamcrest.CoreMatchers;
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
import static cn.edu.xmu.oomall.freight.service.util.TimeFormatter.StrToLocalDateTime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.javaee.core.model.Constants.DATE_TIME_FORMATTER;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(classes = FreightApplication.class)
@AutoConfigureMockMvc
@Transactional
public class ShopOwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedisUtil redisUtil;

    @MockBean
    private LogisticsService logisticsService;

    @MockBean(name="cn.edu.xmu.oomall.freight.service.UndeliverableService")
    private UndeliverableService undeliverableService;

    @MockBean(name="cn.edu.xmu.oomall.freight.service.ShopLogisticsService")
    private ShopLogisticsService shopLogisticsService;

    @MockBean(name = "cn.edu.xmu.oomall.freight.service.WarehouseService")
    private WarehouseService warehouseService;

    @MockBean(name = "cn.edu.xmu.oomall.freight.service.WarehouseLogisticsService")
    private WarehouseLogisticsService warehouseLogisticsService;

    private static String adminToken;

    private static final String CHANNEL = "/channels";

    private static final String UNDELIVER="/shops/{shopId}/shoplogistics/{id}/undeliverableregions";

    JwtHelper jwtHelper = new JwtHelper();

    @BeforeAll
    public static void setup(){
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
    }

    //根据运单号查找快递公司，成功场景
    @Test
    public void findLogisticsByBillCodeTest1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Logistics logistics=new Logistics();
        logistics.setId(3L);
        logistics.setName("中通快递");
        Mockito.when(logisticsService.findLogisticsByBillCode("JT1728392817277")).thenReturn(logistics);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/logistics")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("billCode","JT1728392817277"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id", is(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name",is("中通快递")))
                .andDo(MockMvcResultHandlers.print());
    }

    //根据运单号查找快递公司，运单号无效
    @Test
    public void findLogisticsByBillCodeTest2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Logistics logistics=new Logistics();
        Mockito.when(logisticsService.findLogisticsByBillCode(Mockito.anyString())).thenThrow(new BusinessException(ReturnNo.FREIGHT_BILLCODE_NOTEXIST));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/logistics")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("billCode","JT17283"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.FREIGHT_BILLCODE_NOTEXIST.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 商铺指定不可达地区
     */
    @Test
    public void createUndeliverableTest01() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        UndeliverableVo undeliverableVo = new UndeliverableVo("2022-01-01T12:12:12","2023-01-01T12:12:12");
        UserDto userDto = new UserDto(13L,"test",1l,1);

        LocalDateTime begin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse("2023-01-01T12:12:12", DATE_TIME_FORMATTER);

        UndeliverableDto undeliverableDto = new UndeliverableDto(begin,end,userDto,null,null,null,new RegionDto(13L,"hometown"));
        Mockito.when(undeliverableService.createUndeliverable(13L,2L,undeliverableVo,userDto)).thenReturn(undeliverableDto);

        String body = "{\"beginTime\": \"2022-01-01T12:12:12\",\"endTime\": \"2023-01-01T12:12:12\"}";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/{shopId}/shoplogistics/{id}/regions/{rid}/undeliverable",1L,13L,2L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(body.getBytes("utf-8")))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.CREATED.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }


    /**
     * 更新不可达信息
     */
    @Test
    public void updateUndeliverableTest01() throws Exception {
        Mockito.when(this.redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String body = "{\"beginTime\": \"2022-01-01T12:12:12\",\"endTime\": \"2024-01-01T12:12:12\"}";
        this.mockMvc.perform(MockMvcRequestBuilders
                .put("/shops/{shopId}/shoplogistics/{id}/regions/{rid}/undeliverable", 1L,1L,483250L)
                .header("authorization", new Object[]{adminToken})
                        .content(body.getBytes("utf-8"))
                .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", CoreMatchers.is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }


    /**
     * 删除不可达信息
     */
    @Test
    public void delUndeliverableTest01() throws Exception {
        Mockito.when(this.redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/shops/{shopId}/shoplogistics/{id}/regions/{rid}/undeliverable", new Object[]{1L, 1L, 483250L})
                .header("authorization", new Object[]{adminToken})
                .contentType("application/json"))

                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", CoreMatchers.is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }


    /**
     * 查询不可达信息
     */
    @Test
    public void retreiveUndeliverableTest01() throws Exception {
        Mockito.when(this.redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/{shopId}/shoplogistics/{id}/undeliverableregions", new Object[]{1L, 1L})
                .header("authorization", new Object[]{adminToken})
                .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", CoreMatchers.is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.page", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].regionDto.id", CoreMatchers.is(483250)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].beginTime", CoreMatchers.is("2022-12-02T22:28:43")))
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    public void createShopLogisticsTest01() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        ShopLogisticsVo shopLogisticsVo = new ShopLogisticsVo(1L, "secret1", 10);
        UserDto userDto = new UserDto(12L, "testadmin", 1L, 1);

        ShopLogisticsDto shopLogisticsDto = new ShopLogisticsDto(null, new LogisticsDto(1L, "test"), null, shopLogisticsVo.getSecret(),
                shopLogisticsVo.getPriority(), LocalDateTime.now(), null, userDto, null);
        Mockito.when(shopLogisticsService.createShopLogistics(12L, userDto, shopLogisticsVo)).thenReturn(shopLogisticsDto);

        String body = "{\"logisticsId\": 1,\"secret\": \"secret1\",\"priority\": 10}";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/{shopId}/shoplogistics", 12L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(body.getBytes("utf-8")))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.CREATED.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.logistics.id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.secret", is("secret1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.priority", is(10)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void createWarehouseTest01() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        UserDto userDto = new UserDto(12L, "testadmin", 1L, 1);

        WarehouseVo warehouseVo = new WarehouseVo("Winterfell-Warehouse", "King's Landiing", 1L, "Sansa Stark", "124121212310", 10);

        WareHouseDto wareHouseDto = new WareHouseDto(null, warehouseVo.getAddress(), warehouseVo.getName(),warehouseVo.getSenderName(),warehouseVo.getSenderMobile(),
                0,warehouseVo.getPriority(),LocalDateTime.now(),LocalDateTime.now(),userDto,null,new RegionDto(1L,"BeiJing"));

        Mockito.when(warehouseService.createWarehouses(12L, userDto, warehouseVo)).thenReturn(wareHouseDto);

        String requestBody = "{\"name\":\"Winterfell-Warehouse\",\"address\":\"King's Landiing\",\"regionId\":1,\"senderName\":\"Sansa Stark\",\"senderMobile\":\"124121212310\",\"priority\":10}";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/{shopId}/warehouses", 12L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody.getBytes("utf-8")))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.CREATED.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.region.id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name", is("Winterfell-Warehouse")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.priority", is(10)))
                .andDo(MockMvcResultHandlers.print());
    }



    @Test
    public void retrieveShopLogisticsTest01() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        UserDto userDto = new UserDto(18L, "testadm", 1L, 1);

        ShopLogisticsDto shopLogisticsDto = new ShopLogisticsDto(null, new LogisticsDto(1L, "test"), null, null,
                null, LocalDateTime.now(), null, userDto, null);
        List<ShopLogisticsDto> list = new ArrayList<>();
        list.add(shopLogisticsDto);
        PageDto<ShopLogisticsDto> dto = new PageDto<>(list, 0, 1, 10, 0);

        Mockito.when(shopLogisticsService.retrieveShopLogistics(1L, 1, 10, userDto)).thenReturn(dto);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/{shopId}/shoplogistics", 1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.page", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].logistics.id", is(3)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void retrieveWarehouseTest01() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        UserDto userDto = new UserDto(18L, "testadm", 1L, 1);

        WareHouseDto wareHouseDto = new WareHouseDto();
        wareHouseDto.setRegion(new RegionDto(1L,"BeiJing"));
        wareHouseDto.setCreator(userDto);
        wareHouseDto.setGmtCreate(LocalDateTime.now());


        List<WareHouseDto> list = new ArrayList<>();
        list.add(wareHouseDto);
        PageDto<WareHouseDto> dto = new PageDto<>(list, 0, 1, 10, 0);

        Mockito.when(warehouseService.retrieveWarehouses(1L, 1, 10, userDto)).thenReturn(dto);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/{shopId}/warehouses", 1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.page", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].region.id", is(1043)))
                .andDo(MockMvcResultHandlers.print());
    }



    @Test
    public void updateWarehouseTest01() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        UserDto userDto = new UserDto(12L, "testadmin", 1L, 1);

        WarehouseVo warehouseVo = new WarehouseVo("Winterfell-Warehouse", "King's Landiing", 1L, "Sansa Stark", "124121212310", 10);
        WareHouseDto wareHouseDto = new WareHouseDto(null, warehouseVo.getAddress(), warehouseVo.getName(),warehouseVo.getSenderName(),warehouseVo.getSenderMobile(),
                0,warehouseVo.getPriority(),LocalDateTime.now(),LocalDateTime.now(),userDto,null,new RegionDto(1L,"BeiJing"));

        Mockito.when(warehouseService.updateWarehouses(1L, 1L, userDto, warehouseVo)).thenReturn(wareHouseDto);
        String requestBody = "{\"name\":\"Winterfell-Warehouse\",\"address\":\"King's Landiing\",\"regionId\":1,\"senderName\":\"Sansa Stark\",\"senderMobile\":\"124121212310\",\"priority\":10}";

        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/warehouses/{id}", 1L, 1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody.getBytes("utf-8")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateShopLogisticsTest01() throws Exception{

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        ShopLogisticsVo shopLogisticsVo = new ShopLogisticsVo(null, "secret3", 14);
        UserDto userDto = new UserDto(12L, "testadmin", 1L, 1);

        ShopLogisticsDto shopLogisticsDto = new ShopLogisticsDto(null, new LogisticsDto(1L, "test"), null, shopLogisticsVo.getSecret(),
                shopLogisticsVo.getPriority(), LocalDateTime.now(), null, userDto, null);
        Mockito.when(shopLogisticsService.updateShopLogistics(1L, 1L, userDto, shopLogisticsVo)).thenReturn(shopLogisticsDto);

        String body = "{\"secret\": \"secret3\",\"priority\": 14}";

        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/shoplogistics/{id}", 1L, 1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(body.getBytes("utf-8")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }



    @Test
    public void suspendShopLogisticsTest01() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        UserDto userDto = new UserDto(17L, "testad", 1L, 1);

        ShopLogisticsDto shopLogisticsDto = new ShopLogisticsDto(null, new LogisticsDto(1L, "test"), null, null,
                null, LocalDateTime.now(), null, userDto, null);
        Mockito.when(shopLogisticsService.suspendShopLogistics(1L, 1L, userDto)).thenReturn(shopLogisticsDto);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/shoplogistics/{id}/suspend", 1L, 1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void resumeShopLogisticsTest01() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        UserDto userDto = new UserDto(17L, "test", 1L, 1);

        ShopLogisticsDto shopLogisticsDto = new ShopLogisticsDto(null, new LogisticsDto(1L, "test"), null, null,
                null, LocalDateTime.now(), null, userDto, null);
        Mockito.when(shopLogisticsService.resumeShopLogistics(1L, 1L, userDto)).thenReturn(shopLogisticsDto);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/shoplogistics/{id}/resume", 1L, 1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void delWarehouseTest01() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
//        UserDto userDto = new UserDto(17L, "test", 1L, 1);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/shops/{shopId}/warehouses/{id}", 1L, 1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void createWarehouseLogisticsTest01() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);


        String begin = "2022-01-01T12:12:12";
        String end = "2023-01-01T12:12:12";
        WarehouseLogisticsInfoVo warehouseLogisticsInfoVo = new WarehouseLogisticsInfoVo();
        warehouseLogisticsInfoVo.setBegintime(begin);
        warehouseLogisticsInfoVo.setEndtime(end);

        ShopLogisticsVo shopLogisticsVo = new ShopLogisticsVo(1L, "secret1", 10);
        UserDto userDto = new UserDto(12L, "testadmin", 1L, 1);

        ShopLogisticsDto shopLogisticsDto = new ShopLogisticsDto(null, new LogisticsDto(1L, "test"), null, shopLogisticsVo.getSecret(),
                shopLogisticsVo.getPriority(), LocalDateTime.now(), null, userDto, null);
        WarehouseLogisticsDto warehouseLogisticsDto = new WarehouseLogisticsDto(shopLogisticsDto,StrToLocalDateTime(begin),StrToLocalDateTime(end),null,userDto,
                LocalDateTime.now(),LocalDateTime.now(), userDto);

        Mockito.when(warehouseLogisticsService.createWarehouseLogistics( userDto,1L,1L,1L, warehouseLogisticsInfoVo)).thenReturn(warehouseLogisticsDto);

        String body = "{\"begintime\":\"2022-01-01T12:12:12\",\"endtime\":\"2023-01-01T12:12:12\"}";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/{shopId}/warehouses/{id}/shoplogistics/{lid}", 1L,1L,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(body.getBytes("utf-8")))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.CREATED.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.shopLogistics.id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.shopLogistics.logistics.name", is("顺丰快递")))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void retrieveWarehouseLogisticsTest01() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        UserDto userDto = new UserDto(18L, "testadm", 1L, 1);

        ShopLogisticsDto shopLogisticsDto = new ShopLogisticsDto(null, new LogisticsDto(1L, "test"), null, null,
                null, LocalDateTime.now(), null, userDto, null);

        String begin = "2022-01-01T12:12:12";
        String end = "2023-01-01T12:12:12";

        WarehouseLogisticsDto warehouseLogisticsDto = new WarehouseLogisticsDto(shopLogisticsDto,StrToLocalDateTime(begin),StrToLocalDateTime(end),null,userDto,
                LocalDateTime.now(),LocalDateTime.now(), userDto);
        List< WarehouseLogisticsDto> list = new ArrayList<>();
        list.add(warehouseLogisticsDto);
        PageDto< WarehouseLogisticsDto> dto = new PageDto<>(list, 0, 1, 10, 0);

        Mockito.when(warehouseLogisticsService.retrieveWarehouseLogistics(1L, 1, 10, userDto,1L)).thenReturn(dto);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/{shopId}/warehouses/{id}/shoplogistics", 1L, 1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.page", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].shopLogistics.logistics.id", is(1)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateWarehouseLogisticsTest01() throws Exception{

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        String begin = "2022-01-01T12:12:12";
        String end = "2023-01-01T12:12:12";
        WarehouseLogisticsInfoVo warehouseLogisticsInfoVo = new WarehouseLogisticsInfoVo();
        warehouseLogisticsInfoVo.setBegintime(begin);
        warehouseLogisticsInfoVo.setEndtime(end);

        ShopLogisticsVo shopLogisticsVo = new ShopLogisticsVo(null, "secret3", 14);
        UserDto userDto = new UserDto(12L, "testadmin", 1L, 1);

        ShopLogisticsDto shopLogisticsDto = new ShopLogisticsDto(null, new LogisticsDto(1L, "test"), null, shopLogisticsVo.getSecret(),
                shopLogisticsVo.getPriority(), LocalDateTime.now(), null, userDto, null);

        WarehouseLogisticsDto warehouseLogisticsDto = new WarehouseLogisticsDto(shopLogisticsDto,StrToLocalDateTime(begin),StrToLocalDateTime(end),null,userDto,
                LocalDateTime.now(),LocalDateTime.now(), userDto);
        Mockito.when(warehouseLogisticsService.updateWarehouseLogistics(userDto,1L, 1L, 1L, warehouseLogisticsInfoVo)).thenReturn(warehouseLogisticsDto);

        String body = "{\"begintime\":\"2022-01-01T12:12:12\",\"endtime\":\"2023-01-01T12:12:12\"}";


        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/warehouses/{id}/shoplogistics/{lid}", 1L, 1L,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(body.getBytes("utf-8")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void delWarehouseLogisticsTest01() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/shops/{shopId}/warehouses/{id}/shoplogistics/{lid}", 1L, 1L,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateWarehouseRegion() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        String body = "{\"beginTime\": \"2022-01-01T12:12:12\",\"endTime\": \"2023-01-01T12:12:12\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/warehouses/{wid}/regions/{id}",1L,1L,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(body.getBytes("utf-8")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void createWarehouseRegionTest() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        String requestBody = "{\"beginTime\": \"2022-01-01T12:12:12\",\"endTime\": \"2023-01-01T12:12:12\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/{shopId}/warehouses/{wid}/regions/{id}",1L,9L,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody.getBytes("utf-8")))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.CREATED.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }
}

