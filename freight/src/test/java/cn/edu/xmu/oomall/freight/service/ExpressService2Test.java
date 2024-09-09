package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.ExpressDao;
import cn.edu.xmu.oomall.freight.dao.RegionDao;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.service.dto.ExpressDto;
import cn.edu.xmu.oomall.freight.service.openfeign.JtExpressService;
import cn.edu.xmu.oomall.freight.service.openfeign.JtParam.JtCancelExpressRetObj;
import cn.edu.xmu.oomall.freight.service.openfeign.JtParam.JtGetExpressRetObj;
import cn.edu.xmu.oomall.freight.service.openfeign.SfExpressService;
import cn.edu.xmu.oomall.freight.service.openfeign.SfParam.SfCancelExpressRetObj;
import cn.edu.xmu.oomall.freight.service.openfeign.SfParam.SfGetExpressRetObj;
import cn.edu.xmu.oomall.freight.service.openfeign.ZtoExpressService;
import cn.edu.xmu.oomall.freight.service.openfeign.ZtoParam.ZtoCancelExpressRetObj;
import cn.edu.xmu.oomall.freight.service.openfeign.ZtoParam.ZtoGetExpressRetObj;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class ExpressService2Test {
    @Autowired
    private ExpressService expressService;

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
    //数据库无数据，所以用mockbean
    @MockBean
    private ExpressDao expressDao;


    /**
     * 根据billcode查询运单，成功
     */
    @Test
    public void searchExpressByBillCodeTest1(){
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
        SfGetExpressRetObj sfRet = JacksonUtil.toObj("{\"success\":\"true\",\"errorCode\":\"S0000\",\"errorMsg\":null," +
                "\"msgData\":{\"routeResps\":[{\"mailNo\":\"SF1011603494291\",\"routes\":[{\"acceptTime\":" +
                "\"2019-05-09 10:11:26\",\"acceptAddress\":\"深圳\",\"opcode\":\"50\",\"remark\":\"已派件\"}," +
                "{\"acceptTime\":\"2019-05-09 18:11:26\",\"acceptAddress\":\"深圳\",\"opcode\":\"80\",\"remark\":\"已签收\"}]}]}}", SfGetExpressRetObj.class);
        Mockito.when(sfExpressService.getExpressByBillCode(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(sfRet));
        String s1 = new StringBuilder()
                .append("{\"message\":\"字符串\",\"statusCode\":\"0000\",\"status\":true,\"result\":[")
                .append("{\"scanType\":\"收件\",\"scanDate\":\"1609297452000\",\"desc\":\"【上海】（021-605511111） 的小吉（18888888888） 已揽收\"}")
                .append("]}")
                .toString();
        ZtoGetExpressRetObj ztoRet = JacksonUtil.toObj(s1, ZtoGetExpressRetObj.class);
        Mockito.when(ztoExpressService.getExpressByBillCode(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new InternalReturnObject<>(ztoRet));
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

        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        Mockito.doNothing().when (expressDao).saveById(Mockito.any(),Mockito.any());
        ExpressDto dto= expressService.searchExpressByBillCode(Long.valueOf("2"),"UT0000352320970",user);
        assertThat(dto.getBillCode()).isEqualTo("UT0000498364212");
        assertThat(dto.getId()).isEqualTo(Long.valueOf("1"));
        assertThat(dto.getLogistics().getId()).isEqualTo(Long.valueOf("3"));
        assertThat(dto.getLogistics().getName()).isEqualTo("极兔速递");
        assertThat(dto.getCreator().getId()).isEqualTo(Long.valueOf("2"));
        assertThat(dto.getCreator().getUserName()).isEqualTo("test1");
        assertThat(dto.getModifier().getId()).isEqualTo(Long.valueOf("2"));
        assertThat(dto.getModifier().getUserName()).isEqualTo("test1");
        assertThat(dto.getGmtCreate()).isEqualTo(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        assertThat(dto.getGmtModified()).isEqualTo(LocalDateTime.parse("2022-12-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        assertThat(dto.getStatus()).isEqualTo(Byte.valueOf("2"));
    }

    /**
     * 根据billcode查询运单，billcode运单不属于该店铺
     */
    @Test
    public void searchExpressByBillCodeTest2(){
        Express express= Express.builder().id(Long.valueOf("1")).billCode("UT0000498364212").shopLogisticsId(Long.valueOf("6"))
                .senderRegionId(Long.valueOf("2")).senderName("小九").senderMobile("15546168286").senderAddress("庆丰三路28号")
                .deliverRegionId(Long.valueOf("1")).deliverName("田丽").deliverMobile("13766245825").deliverAddress("站前西路永利酒店斜对面童装店")
                .status(Byte.valueOf("1")).shopId(Long.valueOf("2")).creatorId(Long.valueOf("2")).creatorName("test1").gmtCreate(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME))
                .modifierId(Long.valueOf("2")).modifierName("test1").gmtModified(LocalDateTime.parse("2022-12-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME)).shopLogisticsDao(shopLogisticsDao).regionDao(regionDao).build();
        Mockito.when(expressDao.findByBillCode(Mockito.any())).thenReturn(express);

        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        assertThrows(BusinessException.class,()->expressService.searchExpressByBillCode(Long.valueOf("1"),"UT0000352320970",user));
    }

    /**
     * 根据billcode查询运单，商铺物流不可用
     */
    @Test
    public void searchExpressByBillCodeTest3(){
        Express express= Express.builder().id(Long.valueOf("1")).billCode("UT0000498364212").shopLogisticsId(Long.valueOf("3"))
                .senderRegionId(Long.valueOf("2")).senderName("小九").senderMobile("15546168286").senderAddress("庆丰三路28号")
                .deliverRegionId(Long.valueOf("1")).deliverName("田丽").deliverMobile("13766245825").deliverAddress("站前西路永利酒店斜对面童装店")
                .status(Byte.valueOf("1")).shopId(Long.valueOf("1")).creatorId(Long.valueOf("2")).creatorName("test1").gmtCreate(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME))
                .modifierId(Long.valueOf("2")).modifierName("test1").gmtModified(LocalDateTime.parse("2022-12-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME)).shopLogisticsDao(shopLogisticsDao).regionDao(regionDao).build();
        Mockito.when(expressDao.findByBillCode(Mockito.any())).thenReturn(express);

        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        assertThrows(BusinessException.class,()->expressService.searchExpressByBillCode(Long.valueOf("1"),"UT0000352320970",user));
    }

    /**
     * 根据运单id查询运单，成功
     */
    @Test
    public void searchExpressByExpressIdTest1(){
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
        SfGetExpressRetObj sfRet = JacksonUtil.toObj("{\"success\":\"true\",\"errorCode\":\"S0000\",\"errorMsg\":null," +
                "\"msgData\":{\"routeResps\":[{\"mailNo\":\"SF1011603494291\",\"routes\":[{\"acceptTime\":" +
                "\"2019-05-09 10:11:26\",\"acceptAddress\":\"深圳\",\"opcode\":\"50\",\"remark\":\"已派件\"}," +
                "{\"acceptTime\":\"2019-05-09 18:11:26\",\"acceptAddress\":\"深圳\",\"opcode\":\"80\",\"remark\":\"已签收\"}]}]}}", SfGetExpressRetObj.class);
        Mockito.when(sfExpressService.getExpressByBillCode(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(sfRet));
        String s1 = new StringBuilder()
                .append("{\"message\":\"字符串\",\"statusCode\":\"0000\",\"status\":true,\"result\":[")
                .append("{\"scanType\":\"收件\",\"scanDate\":\"1609297452000\",\"desc\":\"【上海】（021-605511111） 的小吉（18888888888） 已揽收\"}")
                .append("]}")
                .toString();
        ZtoGetExpressRetObj ztoRet = JacksonUtil.toObj(s1, ZtoGetExpressRetObj.class);
        Mockito.when(ztoExpressService.getExpressByBillCode(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new InternalReturnObject<>(ztoRet));

        Express express1= Express.builder().id(Long.valueOf("1")).billCode("SF1011603494291").shopLogisticsId(Long.valueOf("1"))
                .senderRegionId(Long.valueOf("2")).senderName("小九").senderMobile("15546168286").senderAddress("庆丰三路28号")
                .deliverRegionId(Long.valueOf("1")).deliverName("田丽").deliverMobile("13766245825").deliverAddress("站前西路永利酒店斜对面童装店")
                .status(Byte.valueOf("2")).shopId(Long.valueOf("1")).creatorId(Long.valueOf("2")).creatorName("test1").gmtCreate(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME))
                .modifierId(Long.valueOf("2")).modifierName("test1").gmtModified(LocalDateTime.parse("2022-12-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME)).shopLogisticsDao(shopLogisticsDao).regionDao(regionDao).build();
        Mockito.when(expressDao.findById(Mockito.any())).thenReturn(express1);

        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        Mockito.doNothing().when (expressDao).saveById(Mockito.any(),Mockito.any());
        ExpressDto dto= expressService.searchExpressByExpressId(Long.valueOf("1"),user);
        assertThat(dto.getBillCode()).isEqualTo("SF1011603494291");
        assertThat(dto.getId()).isEqualTo(Long.valueOf("1"));
        assertThat(dto.getLogistics().getId()).isEqualTo(Long.valueOf("1"));
        assertThat(dto.getLogistics().getName()).isEqualTo("顺丰快递");
        assertThat(dto.getCreator().getId()).isEqualTo(Long.valueOf("2"));
        assertThat(dto.getCreator().getUserName()).isEqualTo("test1");
        assertThat(dto.getModifier().getId()).isEqualTo(Long.valueOf("2"));
        assertThat(dto.getModifier().getUserName()).isEqualTo("test1");
        assertThat(dto.getGmtCreate()).isEqualTo(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        assertThat(dto.getGmtModified()).isEqualTo(LocalDateTime.parse("2022-12-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        assertThat(dto.getStatus()).isEqualTo(Byte.valueOf("2"));
    }


    /**
     * 根据运单id验收运单——破损，成功
     */
    @Test
    public void confirmExpressByExpressIdTest1(){
        Express express1= Express.builder().id(Long.valueOf("1")).billCode("SF1011603494291").shopLogisticsId(Long.valueOf("1"))
                .senderRegionId(Long.valueOf("2")).senderName("小九").senderMobile("15546168286").senderAddress("庆丰三路28号")
                .deliverRegionId(Long.valueOf("1")).deliverName("田丽").deliverMobile("13766245825").deliverAddress("站前西路永利酒店斜对面童装店")
                .status(Byte.valueOf("5")).shopId(Long.valueOf("1")).creatorId(Long.valueOf("2")).creatorName("test1").gmtCreate(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME))
                .modifierId(Long.valueOf("2")).modifierName("test1").gmtModified(LocalDateTime.parse("2022-12-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME)).shopLogisticsDao(shopLogisticsDao).regionDao(regionDao).build();
        Mockito.when(expressDao.findById(Mockito.any())).thenReturn(express1);
        Mockito.doNothing().when(expressDao).saveById(Mockito.any(),Mockito.any());

        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        expressService.confirmExpressByExpressId(Long.valueOf("1"),Long.valueOf("1"),0,user);

    }

    /**
     * 根据运单id验收运单——破损，状态不可修改为回收
     */
    @Test
    public void confirmExpressByExpressIdTest2(){
        Express express1= Express.builder().id(Long.valueOf("1")).billCode("SF1011603494291").shopLogisticsId(Long.valueOf("1"))
                .senderRegionId(Long.valueOf("2")).senderName("小九").senderMobile("15546168286").senderAddress("庆丰三路28号")
                .deliverRegionId(Long.valueOf("1")).deliverName("田丽").deliverMobile("13766245825").deliverAddress("站前西路永利酒店斜对面童装店")
                .status(Byte.valueOf("2")).shopId(Long.valueOf("1")).creatorId(Long.valueOf("2")).creatorName("test1").gmtCreate(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME))
                .modifierId(Long.valueOf("2")).modifierName("test1").gmtModified(LocalDateTime.parse("2022-12-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME)).shopLogisticsDao(shopLogisticsDao).regionDao(regionDao).build();
        Mockito.when(expressDao.findById(Mockito.any())).thenReturn(express1);
        Mockito.doNothing().when(expressDao).saveById(Mockito.any(),Mockito.any());

        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);

        assertThrows(BusinessException.class,()->expressService.confirmExpressByExpressId(Long.valueOf("1"),Long.valueOf("1"),0,user));

    }

    /**
     * 根据运单id验收运单——破损，运单不是该店铺
     */
    @Test
    public void confirmExpressByExpressIdTest3(){
        Express express1= Express.builder().id(Long.valueOf("1")).billCode("SF1011603494291").shopLogisticsId(Long.valueOf("1"))
                .senderRegionId(Long.valueOf("2")).senderName("小九").senderMobile("15546168286").senderAddress("庆丰三路28号")
                .deliverRegionId(Long.valueOf("1")).deliverName("田丽").deliverMobile("13766245825").deliverAddress("站前西路永利酒店斜对面童装店")
                .status(Byte.valueOf("5")).shopId(Long.valueOf("1")).creatorId(Long.valueOf("2")).creatorName("test1").gmtCreate(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME))
                .modifierId(Long.valueOf("2")).modifierName("test1").gmtModified(LocalDateTime.parse("2022-12-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME)).shopLogisticsDao(shopLogisticsDao).regionDao(regionDao).build();
        Mockito.when(expressDao.findById(Mockito.any())).thenReturn(express1);
        Mockito.doNothing().when(expressDao).saveById(Mockito.any(),Mockito.any());

        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);

        assertThrows(BusinessException.class,()->expressService.confirmExpressByExpressId(Long.valueOf("2"),Long.valueOf("1"),0,user));

    }

    /**
     * 根据运单id验收运单——回收，成功
     */
    @Test
    public void confirmExpressByExpressIdTest4(){
        Express express1= Express.builder().id(Long.valueOf("1")).billCode("SF1011603494291").shopLogisticsId(Long.valueOf("1"))
                .senderRegionId(Long.valueOf("2")).senderName("小九").senderMobile("15546168286").senderAddress("庆丰三路28号")
                .deliverRegionId(Long.valueOf("1")).deliverName("田丽").deliverMobile("13766245825").deliverAddress("站前西路永利酒店斜对面童装店")
                .status(Byte.valueOf("5")).shopId(Long.valueOf("1")).creatorId(Long.valueOf("2")).creatorName("test1").gmtCreate(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME))
                .modifierId(Long.valueOf("2")).modifierName("test1").gmtModified(LocalDateTime.parse("2022-12-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME)).shopLogisticsDao(shopLogisticsDao).regionDao(regionDao).build();
        Mockito.when(expressDao.findById(Mockito.any())).thenReturn(express1);
        Mockito.doNothing().when(expressDao).saveById(Mockito.any(),Mockito.any());

        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);

        expressService.confirmExpressByExpressId(Long.valueOf("1"),Long.valueOf("1"),1,user);

    }

    /**
     * 取消发货，成功
     */
    @Test
    public void cancelExpressByExpressIdTest1(){
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

        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);

        expressService.cancelExpressByExpressId(Long.valueOf("1"),Long.valueOf("1"),user);
    }

    /**
     * 取消发货，运单不是该商铺的
     */
    @Test
    public void cancelExpressByExpressIdTest2(){
        Express express= Express.builder().id(Long.valueOf("1")).billCode("SF1011603494291").shopLogisticsId(Long.valueOf("1"))
                .senderRegionId(Long.valueOf("2")).senderName("小九").senderMobile("15546168286").senderAddress("庆丰三路28号")
                .deliverRegionId(Long.valueOf("1")).deliverName("田丽").deliverMobile("13766245825").deliverAddress("站前西路永利酒店斜对面童装店")
                .status(Byte.valueOf("0")).shopId(Long.valueOf("2")).creatorId(Long.valueOf("2")).creatorName("test1").gmtCreate(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME))
                .modifierId(Long.valueOf("2")).modifierName("test1").gmtModified(LocalDateTime.parse("2022-12-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME)).shopLogisticsDao(shopLogisticsDao).regionDao(regionDao).build();
        Mockito.when(expressDao.findById(Mockito.any())).thenReturn(express);
        Mockito.doNothing().when(expressDao).saveById(Mockito.any(),Mockito.any());

        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);

        assertThrows(BusinessException.class,()->expressService.cancelExpressByExpressId(Long.valueOf("1"),Long.valueOf("1"),user)) ;
    }

    /**
     * 取消发货，运单状态不可取消
     */
    @Test
    public void cancelExpressByExpressIdTest3(){
        Express express= Express.builder().id(Long.valueOf("1")).billCode("SF1011603494291").shopLogisticsId(Long.valueOf("1"))
                .senderRegionId(Long.valueOf("2")).senderName("小九").senderMobile("15546168286").senderAddress("庆丰三路28号")
                .deliverRegionId(Long.valueOf("1")).deliverName("田丽").deliverMobile("13766245825").deliverAddress("站前西路永利酒店斜对面童装店")
                .status(Byte.valueOf("1")).shopId(Long.valueOf("1")).creatorId(Long.valueOf("2")).creatorName("test1").gmtCreate(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME))
                .modifierId(Long.valueOf("2")).modifierName("test1").gmtModified(LocalDateTime.parse("2022-12-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME)).shopLogisticsDao(shopLogisticsDao).regionDao(regionDao).build();
        Mockito.when(expressDao.findById(Mockito.any())).thenReturn(express);
        Mockito.doNothing().when(expressDao).saveById(Mockito.any(),Mockito.any());

        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);

        assertThrows(BusinessException.class,()->expressService.cancelExpressByExpressId(Long.valueOf("1"),Long.valueOf("1"),user)) ;
    }

    /**
     * 取消发货，运单对应物流不可用
     */
    @Test
    public void cancelExpressByExpressIdTest4(){
        Express express= Express.builder().id(Long.valueOf("1")).billCode("SF1011603494291").shopLogisticsId(Long.valueOf("3"))
                .senderRegionId(Long.valueOf("2")).senderName("小九").senderMobile("15546168286").senderAddress("庆丰三路28号")
                .deliverRegionId(Long.valueOf("1")).deliverName("田丽").deliverMobile("13766245825").deliverAddress("站前西路永利酒店斜对面童装店")
                .status(Byte.valueOf("0")).shopId(Long.valueOf("1")).creatorId(Long.valueOf("2")).creatorName("test1").gmtCreate(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME))
                .modifierId(Long.valueOf("2")).modifierName("test1").gmtModified(LocalDateTime.parse("2022-12-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME)).shopLogisticsDao(shopLogisticsDao).regionDao(regionDao).build();
        Mockito.when(expressDao.findById(Mockito.any())).thenReturn(express);
        Mockito.doNothing().when(expressDao).saveById(Mockito.any(),Mockito.any());

        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);

        assertThrows(BusinessException.class,()->expressService.cancelExpressByExpressId(Long.valueOf("1"),Long.valueOf("1"),user)) ;
    }

    /**
     * 取消发货，第三方API取消失败
     */
    @Test
    public void cancelExpressByExpressIdTest5(){
        JtCancelExpressRetObj jtRet= JacksonUtil.toObj("{\"code\":\"0\",\"msg\":\"失败\",\"data\":{}}", JtCancelExpressRetObj.class);
        Mockito.when(jtExpressService.cancelExpress(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(jtRet));
        SfCancelExpressRetObj sfRet = JacksonUtil.toObj("{\"success\":\"false\",\"errorCode\":\"S0000\",\"errorMsg\":null," +
                "\"msgData\":{}}", SfCancelExpressRetObj.class);
        Mockito.when(sfExpressService.cancelExpress(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(sfRet));
        Express express1= Express.builder().id(Long.valueOf("1")).billCode("SF1011603494291").shopLogisticsId(Long.valueOf("1"))
                .senderRegionId(Long.valueOf("2")).senderName("小九").senderMobile("15546168286").senderAddress("庆丰三路28号")
                .deliverRegionId(Long.valueOf("1")).deliverName("田丽").deliverMobile("13766245825").deliverAddress("站前西路永利酒店斜对面童装店")
                .status(Byte.valueOf("0")).shopId(Long.valueOf("1")).creatorId(Long.valueOf("2")).creatorName("test1").gmtCreate(LocalDateTime.parse("2022-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME))
                .modifierId(Long.valueOf("2")).modifierName("test1").gmtModified(LocalDateTime.parse("2022-12-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME)).shopLogisticsDao(shopLogisticsDao).regionDao(regionDao).build();
        Mockito.when(expressDao.findById(Mockito.any())).thenReturn(express1);
        ZtoCancelExpressRetObj ztoRet = JacksonUtil.toObj("{\"message\":\"字符串\",\"statusCode\":\"0001\",\"status\":true,\"result\":{}}", ZtoCancelExpressRetObj.class);
        Mockito.when(ztoExpressService.cancelExpress(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new InternalReturnObject<>(ztoRet));
        Mockito.doNothing().when(expressDao).saveById(Mockito.any(),Mockito.any());

        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);

        assertThrows(BusinessException.class,()->expressService.cancelExpressByExpressId(Long.valueOf("1"),Long.valueOf("1"),user));
    }

}
