package cn.edu.xmu.oomall.freight.service.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.service.courier.dto.CancelExpressAdaptorDto;
import cn.edu.xmu.oomall.freight.service.courier.dto.GetExpressAdaptorDto;
import cn.edu.xmu.oomall.freight.service.openfeign.JtParam.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class JtExpressServiceTest {
    @MockBean
    private JtExpressService jtExpressService;
    @Test
    public void postExpressTest(){
        JtPostExpressParam param=new JtPostExpressParam();
        JtPostExpressRetObj ret= JacksonUtil.toObj(
                "{\"code\":\"1\",\"msg\":\"success\",\"data\":{\"txlogisticId\":\"TEST20220704210006\",\"billCode\":\"UT0000498364212\",\"sortingCode\":\"382 300-64 010\",\"sumFreight\":\"5.00\" ,\"createOrderTime\":\"2022-07-04 12:00:53\",\"lastCenterName\":\"华东转运中心B1\"}}",JtPostExpressRetObj.class);
        Mockito.when(jtExpressService.postExpress(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(ret));
        JtPostExpressRetObj ret1= jtExpressService.postExpress("JT11122",null,Long.valueOf("1111111"),param).getData();
        assertThat(ret1.getCode()).isEqualTo("1");
        assertThat(ret1.getMsg()).isEqualTo("success");
        assertThat(ret1.getData().getBillCode()).isEqualTo("UT0000498364212");
    }

    @Test
    public void getExpressTest(){
        JtGetExpressParam param=new JtGetExpressParam();
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
        JtGetExpressRetObj ret1= jtExpressService.getExpressByBillCode("JT11122",null,Long.valueOf("1111111"),param).getData();
        assertThat(ret1.getCode()).isEqualTo("1");
        assertThat(ret1.getMsg()).isEqualTo("success");
        assertThat(ret1.getData().get(0).getDetails().get(0).getDesc()).isEqualTo("包裹已签收！签收人是【本人签收】，如有疑问请联系：13123456789，如需联系网点请拨打：17314954950 特 殊时期，极兔从不懈怠，感谢使用，我们时刻准备，再次为您服务！");
        assertThat(ret1.getByteStatus()).isEqualTo(Byte.valueOf("2"));
    }

    @Test
    public void cancelExpressTest(){
        JtCancelExpressParam param=new JtCancelExpressParam();
        JtCancelExpressRetObj ret= JacksonUtil.toObj("{\"code\":\"1\",\"msg\":\"success\",\"data\":{\"txlogisticId\":\"1598516207047\",\"billCode\":\"UT0000272932121\"}}", JtCancelExpressRetObj.class);
        Mockito.when(jtExpressService.cancelExpress(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(ret));
        JtCancelExpressRetObj ret1= jtExpressService.cancelExpress("JT11122",null,Long.valueOf("1111111"),param).getData();
        assertThat(ret1.getCode()).isEqualTo("1");
        assertThat(ret1.getMsg()).isEqualTo("success");

    }
}
