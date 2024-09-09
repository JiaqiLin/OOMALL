package cn.edu.xmu.oomall.freight.service.courier;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.RegionDao;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.service.courier.dto.CancelExpressAdaptorDto;
import cn.edu.xmu.oomall.freight.service.courier.dto.GetExpressAdaptorDto;
import cn.edu.xmu.oomall.freight.service.courier.dto.PostExpressAdaptorDto;
import cn.edu.xmu.oomall.freight.service.openfeign.JtExpressService;
import cn.edu.xmu.oomall.freight.service.openfeign.JtParam.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class JtExpressAdaptorTest {
    @Autowired
    private JtExpressAdaptor jtExpressAdaptor;
    @Autowired
    private ShopLogisticsDao shopLogisticsDao;
    @Autowired
    private RegionDao regionDao;
    @MockBean
    private JtExpressService jtExpressService;
    @Test
    public void createExpressTest1(){
        JtPostExpressRetObj ret= JacksonUtil.toObj(
                "{\"code\":\"1\",\"msg\":\"success\",\"data\":{\"txlogisticId\":\"TEST20220704210006\",\"billCode\":\"UT0000498364212\",\"sortingCode\":\"382 300-64 010\",\"sumFreight\":\"5.00\" ,\"createOrderTime\":\"2022-07-04 12:00:53\",\"lastCenterName\":\"华东转运中心B1\"}}",JtPostExpressRetObj.class);
        Mockito.when(jtExpressService.postExpress(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(ret));
        Express express=new Express();
        express.setId(Long.valueOf("1"));
        express.setShopLogisticsId(Long.valueOf("1"));
        express.setShopLogisticsDao(shopLogisticsDao);
        express.setRegionDao(regionDao);
        express.setDeliverName("田丽");
        express.setDeliverMobile("13766245825");
        express.setDeliverAddress("站前西路永利酒店斜对面童装店");
        express.setDeliverRegionId(Long.valueOf("1"));
        express.setSenderName("小九");
        express.setSenderMobile("15546168286");
        express.setSenderAddress("庆丰三路28号");
        express.setSenderRegionId(Long.valueOf("2"));
        PostExpressAdaptorDto dto = jtExpressAdaptor.createExpress(express);
        assertThat(dto.getBillCode()).isEqualTo("UT0000498364212");
    }

    @Test
    public void createExpressTest2(){
        JtPostExpressRetObj ret= JacksonUtil.toObj(
                "{\"code\":\"0\",\"msg\":\"失败\",\"data\":{}}",JtPostExpressRetObj.class);
        Mockito.when(jtExpressService.postExpress(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(ret));
        Express express=new Express();
        express.setId(Long.valueOf("1"));
        express.setShopLogisticsId(Long.valueOf("1"));
        express.setShopLogisticsDao(shopLogisticsDao);
        express.setRegionDao(regionDao);
        express.setDeliverName("田丽");
        express.setDeliverMobile("13766245825");
        express.setDeliverAddress("站前西路永利酒店斜对面童装店");
        express.setDeliverRegionId(Long.valueOf("1"));
        express.setSenderName("小九");
        express.setSenderMobile("15546168286");
        express.setSenderAddress("庆丰三路28号");
        express.setSenderRegionId(Long.valueOf("2"));
        assertThrows(BusinessException.class,()->jtExpressAdaptor.createExpress(express));
    }

    @Test
    public void returnExpressByBillCodeTest1(){
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
        JtGetExpressRetObj ret= JacksonUtil.toObj(s, JtGetExpressRetObj.class);
        Mockito.when(jtExpressService.getExpressByBillCode(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(ret));
        Express express=new Express();
        express.setBillCode("UT0000352320970");
        express.setShopLogisticsId(Long.valueOf("1"));
        express.setShopLogisticsDao(shopLogisticsDao);
        GetExpressAdaptorDto dto = jtExpressAdaptor.returnExpressByBillCode(express);
        assertThat(dto.getStatus()).isEqualTo(Byte.valueOf("2"));
        assertThat(dto.getRoutes().get(0).getGmtCreate()).isEqualTo("2020-07-18T08:53:05");
        assertThat(dto.getRoutes().get(0).getContent()).isEqualTo("包裹已签收！签收人是【本人签收】，如有疑问请联系：13123456789，如需联系网点请拨打：17314954950 特 殊时期，极兔从不懈怠，感谢使用，我们时刻准备，再次为您服务！");
    }

    @Test
    public void returnExpressByBillCodeTest2(){
        String s = new StringBuilder()
                .append("{\"code\":\"0\",\"msg\":\"失败\",\"data\":[]}")
                .toString();
        JtGetExpressRetObj ret= JacksonUtil.toObj(s, JtGetExpressRetObj.class);
        Mockito.when(jtExpressService.getExpressByBillCode(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(ret));
        Express express=new Express();
        express.setBillCode("UT0000352320970");
        express.setShopLogisticsId(Long.valueOf("1"));
        express.setShopLogisticsDao(shopLogisticsDao);
        assertThrows(BusinessException.class,()->jtExpressAdaptor.returnExpressByBillCode(express));
    }
    @Test
    public void cancelExpressTest1(){
        JtCancelExpressRetObj ret= JacksonUtil.toObj("{\"code\":\"1\",\"msg\":\"success\",\"data\":{\"txlogisticId\":\"1598516207047\",\"billCode\":\"UT0000272932121\"}}", JtCancelExpressRetObj.class);
        Mockito.when(jtExpressService.cancelExpress(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(ret));
        Express express=new Express();
        express.setId(Long.valueOf("1"));
        express.setShopLogisticsId(Long.valueOf("1"));
        express.setShopLogisticsDao(shopLogisticsDao);
        CancelExpressAdaptorDto dto = jtExpressAdaptor.cancelExpress(express);
        assertThat(dto.getStatus()).isEqualTo(true);
    }

    @Test
    public void cancelExpressTest2(){
        JtCancelExpressRetObj ret= JacksonUtil.toObj("{\"code\":\"0\",\"msg\":\"失败\",\"data\":{}}", JtCancelExpressRetObj.class);
        Mockito.when(jtExpressService.cancelExpress(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(ret));
        Express express=new Express();
        express.setId(Long.valueOf("1"));
        express.setShopLogisticsId(Long.valueOf("1"));
        express.setShopLogisticsDao(shopLogisticsDao);
        assertThrows(BusinessException.class,()->jtExpressAdaptor.cancelExpress(express));
    }

}
