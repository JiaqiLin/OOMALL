package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.service.openfeign.JtExpressService;
import cn.edu.xmu.oomall.freight.service.openfeign.JtParam.JtPostExpressRetObj;
import cn.edu.xmu.oomall.freight.service.openfeign.SfExpressService;
import cn.edu.xmu.oomall.freight.service.openfeign.SfParam.SfPostExpressRetObj;
import cn.edu.xmu.oomall.freight.service.openfeign.ZtoExpressService;
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

import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(classes = FreightApplication.class)
@AutoConfigureMockMvc
@Transactional
public class InternalExpressController1Test {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JtExpressService jtExpressService;
    @MockBean
    private SfExpressService sfExpressService;
    @MockBean
    private ZtoExpressService ztoExpressService;
    @MockBean
    private RedisUtil redisUtil;
    private static String adminToken;

    private static final String CREATE = "/internal/shops/{shopId}/packages";

    private static final String SEARCHBYBILLCODE="/internal/shops/{shopId}/packages";

    private static final String SEARCHBYEXPRESSID = "/internal/packages/{id}";

    private static final String CONFIRM="/internal/shops/{shopId}/packages/{id}/confirm";

    private static final String CANCEL="/internal/shops/{shopId}/packages/{id}/cancel";

    @BeforeAll
    public static void setup(){
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
    }

    @Test
    public void createExpressTest1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        SfPostExpressRetObj sfRet = JacksonUtil.toObj("{\"success\":\"true\",\"errorCode\":\"S0000\",\"errorMsg\":null," +
                "\"msgData\":{\"orderId\":\"QIAO-20200528-006\",\"waybillNoInfoList\":[{\"waybillType\":1,\"waybillNo\":\"SF7444400043266\"}]}}", SfPostExpressRetObj.class);
        Mockito.when(sfExpressService.postExpress(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(sfRet));
        String body = "{\"shopLogisticsId\": 0,\"sender\": {},\"deliver\": {\"name\": \"田丽\",\"mobile\": \"13766245825\",\"regionId\": 1,\"address\": \"站前西路永利酒店斜对面童装店\"}}";
        this.mockMvc.perform(MockMvcRequestBuilders.post(CREATE,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(body.getBytes("utf-8")))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.CREATED.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.billCode",is("SF7444400043266")))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

    }

    @Test
    public void createExpressTest2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        JtPostExpressRetObj jtRet= JacksonUtil.toObj(
                "{\"code\":\"1\",\"msg\":\"success\",\"data\":{\"txlogisticId\":\"TEST20220704210006\",\"billCode\":\"UT0000498364212\",\"sortingCode\":\"382 300-64 010\",\"sumFreight\":\"5.00\" ,\"createOrderTime\":\"2022-07-04 12:00:53\",\"lastCenterName\":\"华东转运中心B1\"}}",JtPostExpressRetObj.class);
        Mockito.when(jtExpressService.postExpress(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(jtRet));
        String body = "{\"shopLogisticsId\": 6,\"sender\": { \"name\": \"小九\",\"mobile\": \"15546168286\", \"regionId\": 2,\"address\": \"庆丰三路28号\"},\"deliver\": {\"name\": \"田丽\",\"mobile\": \"13766245825\",\"regionId\": 1,\"address\": \"庆丰三路28号\"}}";
        this.mockMvc.perform(MockMvcRequestBuilders.post(CREATE,2L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(body.getBytes("utf-8")))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.CREATED.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.billCode",is("UT0000498364212")));

    }


}
