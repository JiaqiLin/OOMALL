package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.ExpressDao;
import cn.edu.xmu.oomall.freight.dao.RegionDao;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.service.ExpressService;
import cn.edu.xmu.oomall.freight.service.openfeign.JtExpressService;
import cn.edu.xmu.oomall.freight.service.openfeign.JtParam.JtCancelExpressRetObj;
import cn.edu.xmu.oomall.freight.service.openfeign.JtParam.JtGetExpressRetObj;
import cn.edu.xmu.oomall.freight.service.openfeign.SfExpressService;
import cn.edu.xmu.oomall.freight.service.openfeign.SfParam.SfCancelExpressRetObj;
import cn.edu.xmu.oomall.freight.service.openfeign.ZtoExpressService;
import cn.edu.xmu.oomall.freight.service.openfeign.ZtoParam.ZtoCancelExpressRetObj;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(classes = FreightApplication.class)
@AutoConfigureMockMvc
@Transactional
public class InternalExpressController2Test {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ExpressService expressService;
    //数据库无数据，所以用mockbean
    @MockBean
    private ExpressDao expressDao;
    @Autowired
    private ShopLogisticsDao shopLogisticsDao;

    @Autowired
    private RegionDao regionDao;
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
    public void searchExpressByBillCodeTest() throws Exception {
        String s = new StringBuilder()
                .append("{\"code\":\"1\",\"msg\":\"success\",\"data\":[{")
                .append("\"billCode\":\"UT0000352320970\",\"details\":[")
                .append("{\"billCode\":\"UT0000352320970\",\"scanTime\":\"2020-07-18 08:53:05\",\"desc\":\"包裹已签收！签收人是【本人签收】，如有疑问请联系：13123456789，如需联系网点请拨打：17314954950 特 殊时期，极兔从不懈怠，感谢使用，我们时刻准备，再次为您服务！\",\"scanType\":\"快件签收\",\"problemType\":null},")
                .append("{\"billCode\":\"UT0000352320970\",\"scanTime\":\"2020-07-18 08:52:53\",\"desc\":\"快件离开【南京转运中心】已发往【南京玄武网点】，您的极兔包，离目的地更近一步啦！\",\"scanType\":\"发件扫描\",\"problemType\":null},")
                .append("{\"billCode\":\"UT0000352320970\",\"scanTime\":\"2020-07-18 08:52:51\",\"desc\":\"快件到达【南京转运中心】，您的极兔包，离目的地更近一步啦！\",\"scanType\":\"到件扫描\",\"problemType\":null},")
                .append("{\"billCode\":\"UT0000352320970\",\"scanTime\":\"2020-07-18 08:52:50\",\"desc\":\"快件离开【南京雨花台春江新城网点】已发往【南京转运中心】，您的极兔包，离目的地更近一步啦！\",\"scanType\":\"发件扫描\",\"problemType\":null},")
                .append("{\"billCode\":\"UT0000352320970\",\"scanTime\":\"2020-07-18 08:52:48\",\"desc\":\"包裹顺利到达【南京雨花台春江新城网点】15850664590，放心交给极兔小哥吧！\",\"scanType\":\"入仓扫描\",\"problemType\":null},")
                .append("{\"billCode\":\"UT0000352320970\",\"scanTime\":\"2020-07-18 08:52:43\",\"desc\":\"【南京雨花台春江新城网点】您的极兔小哥test1040(13123456789)已取件。如需联系网点，请拨打 15850664590 特殊时期，您的牵挂，让极兔小哥为您速递！ᕱ⑅ᕱ\",\"scanType\":\"快件揽收\",\"problemType\":null}")
                .append("]}]}")
                .toString();
        JtGetExpressRetObj jtRet= JacksonUtil.toObj(s, JtGetExpressRetObj.class);
        Mockito.when(jtExpressService.getExpressByBillCode(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(jtRet));
        Express express= Express.builder().id(Long.valueOf("1")).billCode("UT0000498364212").shopLogisticsId(Long.valueOf("6"))
                .senderRegionId(Long.valueOf("2")).senderName("小九").senderMobile("15546168286").senderAddress("庆丰三路28号")
                .deliverRegionId(Long.valueOf("1")).deliverName("田丽").deliverMobile("13766245825").deliverAddress("站前西路永利酒店斜对面童装店")
                .status(Byte.valueOf("1")).shopId(Long.valueOf("2")).creatorId(Long.valueOf("2")).creatorName("test1").gmtCreate(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME))
                .modifierId(Long.valueOf("2")).modifierName("test1").gmtModified(LocalDateTime.parse("2022-12-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME)).shopLogisticsDao(shopLogisticsDao).regionDao(regionDao).build();
        Mockito.when(expressDao.findByBillCode(Mockito.any())).thenReturn(express);
        Express express1= Express.builder().id(Long.valueOf("1")).billCode("UT0000498364212").shopLogisticsId(Long.valueOf("6"))
                .senderRegionId(Long.valueOf("2")).senderName("小九").senderMobile("15546168286").senderAddress("庆丰三路28号")
                .deliverRegionId(Long.valueOf("1")).deliverName("田丽").deliverMobile("13766245825").deliverAddress("站前西路永利酒店斜对面童装店")
                .status(Byte.valueOf("2")).shopId(Long.valueOf("2")).creatorId(Long.valueOf("2")).creatorName("test1").gmtCreate(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME))
                .modifierId(Long.valueOf("2")).modifierName("test1").gmtModified(LocalDateTime.parse("2022-12-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME)).shopLogisticsDao(shopLogisticsDao).regionDao(regionDao).build();
        Mockito.when(expressDao.findById(Mockito.any())).thenReturn(express1);
        Mockito.doNothing().when (expressDao).saveById(Mockito.any(),Mockito.any());
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        this.mockMvc.perform(MockMvcRequestBuilders.get(SEARCHBYBILLCODE,2L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("billCode","UT0000498364212"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.billCode",is("UT0000498364212")));

    }

    @Test
    public void searchExpressByExpressIdTest() throws Exception {
        String s = new StringBuilder()
                .append("{\"code\":\"1\",\"msg\":\"success\",\"data\":[{")
                .append("\"billCode\":\"UT0000352320970\",\"details\":[")
                .append("{\"billCode\":\"UT0000352320970\",\"scanTime\":\"2020-07-18 08:53:05\",\"desc\":\"包裹已签收！签收人是【本人签收】，如有疑问请联系：13123456789，如需联系网点请拨打：17314954950 特 殊时期，极兔从不懈怠，感谢使用，我们时刻准备，再次为您服务！\",\"scanType\":\"快件签收\",\"problemType\":null},")
                .append("{\"billCode\":\"UT0000352320970\",\"scanTime\":\"2020-07-18 08:52:53\",\"desc\":\"快件离开【南京转运中心】已发往【南京玄武网点】，您的极兔包，离目的地更近一步啦！\",\"scanType\":\"发件扫描\",\"problemType\":null},")
                .append("{\"billCode\":\"UT0000352320970\",\"scanTime\":\"2020-07-18 08:52:51\",\"desc\":\"快件到达【南京转运中心】，您的极兔包，离目的地更近一步啦！\",\"scanType\":\"到件扫描\",\"problemType\":null},")
                .append("{\"billCode\":\"UT0000352320970\",\"scanTime\":\"2020-07-18 08:52:50\",\"desc\":\"快件离开【南京雨花台春江新城网点】已发往【南京转运中心】，您的极兔包，离目的地更近一步啦！\",\"scanType\":\"发件扫描\",\"problemType\":null},")
                .append("{\"billCode\":\"UT0000352320970\",\"scanTime\":\"2020-07-18 08:52:48\",\"desc\":\"包裹顺利到达【南京雨花台春江新城网点】15850664590，放心交给极兔小哥吧！\",\"scanType\":\"入仓扫描\",\"problemType\":null},")
                .append("{\"billCode\":\"UT0000352320970\",\"scanTime\":\"2020-07-18 08:52:43\",\"desc\":\"【南京雨花台春江新城网点】您的极兔小哥test1040(13123456789)已取件。如需联系网点，请拨打 15850664590 特殊时期，您的牵挂，让极兔小哥为您速递！ᕱ⑅ᕱ\",\"scanType\":\"快件揽收\",\"problemType\":null}")
                .append("]}]}")
                .toString();
        JtGetExpressRetObj jtRet= JacksonUtil.toObj(s, JtGetExpressRetObj.class);
        Mockito.when(jtExpressService.getExpressByBillCode(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(jtRet));
        Express express1= Express.builder().id(Long.valueOf("1")).billCode("UT0000498364212").shopLogisticsId(Long.valueOf("6"))
                .senderRegionId(Long.valueOf("2")).senderName("小九").senderMobile("15546168286").senderAddress("庆丰三路28号")
                .deliverRegionId(Long.valueOf("1")).deliverName("田丽").deliverMobile("13766245825").deliverAddress("站前西路永利酒店斜对面童装店")
                .status(Byte.valueOf("2")).shopId(Long.valueOf("2")).creatorId(Long.valueOf("2")).creatorName("test1").gmtCreate(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME))
                .modifierId(Long.valueOf("2")).modifierName("test1").gmtModified(LocalDateTime.parse("2022-12-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME)).shopLogisticsDao(shopLogisticsDao).regionDao(regionDao).build();
        Mockito.when(expressDao.findById(Mockito.any())).thenReturn(express1);
        Mockito.doNothing().when (expressDao).saveById(Mockito.any(),Mockito.any());
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        this.mockMvc.perform(MockMvcRequestBuilders.get(SEARCHBYEXPRESSID,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("billCode","UT0000498364212"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.billCode",is("UT0000498364212")));

    }

    @Test
    public void confirmExpressByExpressIdTest() throws Exception {
        Express express1= Express.builder().id(Long.valueOf("1")).billCode("SF1011603494291").shopLogisticsId(Long.valueOf("1"))
                .senderRegionId(Long.valueOf("2")).senderName("小九").senderMobile("15546168286").senderAddress("庆丰三路28号")
                .deliverRegionId(Long.valueOf("1")).deliverName("田丽").deliverMobile("13766245825").deliverAddress("站前西路永利酒店斜对面童装店")
                .status(Byte.valueOf("5")).shopId(Long.valueOf("1")).creatorId(Long.valueOf("2")).creatorName("test1").gmtCreate(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME))
                .modifierId(Long.valueOf("2")).modifierName("test1").gmtModified(LocalDateTime.parse("2022-12-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME)).shopLogisticsDao(shopLogisticsDao).regionDao(regionDao).build();
        Mockito.when(expressDao.findById(Mockito.any())).thenReturn(express1);
        Mockito.doNothing().when(expressDao).saveById(Mockito.any(),Mockito.any());
        String body = "{\"status\": 0}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(CONFIRM,1L,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(body.getBytes("utf-8")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())));
    }

    @Test
    public void cancelExpressByExpressIdTest() throws Exception {
        JtCancelExpressRetObj jtRet= JacksonUtil.toObj("{\"code\":\"1\",\"msg\":\"success\",\"data\":{\"txlogisticId\":\"1598516207047\",\"billCode\":\"UT0000272932121\"}}", JtCancelExpressRetObj.class);
        Mockito.when(jtExpressService.cancelExpress(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(jtRet));
        SfCancelExpressRetObj sfRet = JacksonUtil.toObj("{\"success\":\"true\",\"errorCode\":\"S0000\",\"errorMsg\":null," +
                "\"msgData\":{\"orderId\":\"eb21c793-a45a-4d1e-9a2e-1b6e0cd49668\",\"waybillNoInfoList\"" +
                ":[{\"waybillType\":1,\"waybillNo\":\"SF7444400043064\"}],\"resStatus\":2,\"extraInfoList\":null}}", SfCancelExpressRetObj.class);
        Mockito.when(sfExpressService.cancelExpress(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(sfRet));
        Express express1= Express.builder().id(Long.valueOf("1")).billCode("SF1011603494291").shopLogisticsId(Long.valueOf("1"))
                .senderRegionId(Long.valueOf("2")).senderName("小九").senderMobile("15546168286").senderAddress("庆丰三路28号")
                .deliverRegionId(Long.valueOf("1")).deliverName("田丽").deliverMobile("13766245825").deliverAddress("站前西路永利酒店斜对面童装店")
                .status(Byte.valueOf("0")).shopId(Long.valueOf("1")).creatorId(Long.valueOf("2")).creatorName("test1").gmtCreate(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME))
                .modifierId(Long.valueOf("2")).modifierName("test1").gmtModified(LocalDateTime.parse("2022-12-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME)).shopLogisticsDao(shopLogisticsDao).regionDao(regionDao).build();
        Mockito.when(expressDao.findById(Mockito.any())).thenReturn(express1);
        ZtoCancelExpressRetObj ztoRet = JacksonUtil.toObj("{\"message\":\"字符串\",\"statusCode\":\"0000\",\"status\":true,\"result\":{}}", ZtoCancelExpressRetObj.class);
        Mockito.when(ztoExpressService.cancelExpress(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new InternalReturnObject<>(ztoRet));
        Mockito.doNothing().when(expressDao).saveById(Mockito.any(),Mockito.any());
        this.mockMvc.perform(MockMvcRequestBuilders.put(CANCEL,1L,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())));
    }
}
