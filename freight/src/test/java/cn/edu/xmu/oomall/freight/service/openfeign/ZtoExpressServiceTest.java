package cn.edu.xmu.oomall.freight.service.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.service.openfeign.ZtoParam.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class ZtoExpressServiceTest {
    @MockBean
    private  ZtoExpressService ztoExpressService;
    @Test
    public void postExpressTest(){
        ZtoPostExpressParam param = new ZtoPostExpressParam();
        ZtoPostExpressRetObj ret = JacksonUtil.toObj(
                "{\"message\":\"字符串\",\"statusCode\":\"0000\",\"status\":true,\"result\":{\"billCode\":\"130005102254\"}}", ZtoPostExpressRetObj.class);
        Mockito.when(ztoExpressService.postExpress(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new InternalReturnObject<>(ret));
        ZtoPostExpressRetObj ret1 = ztoExpressService.postExpress("1122365", "12345678", param).getData();
        assertThat(ret1.getStatusCode()).isEqualTo("0000");
        assertThat(ret1.getStatus()).isEqualTo(true);
        assertThat(ret1.getResult().getBillCode()).isEqualTo("130005102254");
    }

    @Test
    public void getExpressTest(){
        ZtoGetExpressParam param = new ZtoGetExpressParam();
        String s = new StringBuilder()
                .append("{\"message\":\"字符串\",\"statusCode\":\"0000\",\"status\":true,\"result\":[")
                .append("{\"scanType\":\"收件\",\"scanDate\":\"1609297452000\",\"desc\":\"【上海】（021-605511111） 的小吉（18888888888） 已揽收\"}")
                .append("]}")
                .toString();
        ZtoGetExpressRetObj ret = JacksonUtil.toObj(s, ZtoGetExpressRetObj.class);
        Mockito.when(ztoExpressService.getExpressByBillCode(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new InternalReturnObject<>(ret));
        ZtoGetExpressRetObj ret1 = ztoExpressService.getExpressByBillCode("1122365", "12345678", param).getData();
        assertThat(ret1.getStatusCode()).isEqualTo("0000");
        assertThat(ret1.getStatus()).isEqualTo(true);
        assertThat(ret1.getResult().get(0).getDesc()).isEqualTo("【上海】（021-605511111） 的小吉（18888888888） 已揽收");
        assertThat(ret1.getByteStatus()).isEqualTo(Byte.valueOf("6"));
    }

    @Test
    public void cancelExpressTest(){
        ZtoCancelExpressParam param = new ZtoCancelExpressParam();
        ZtoCancelExpressRetObj ret = JacksonUtil.toObj("{\"message\":\"字符串\",\"statusCode\":\"0000\",\"status\":true,\"result\":{}}", ZtoCancelExpressRetObj.class);
        Mockito.when(ztoExpressService.cancelExpress(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new InternalReturnObject<>(ret));
        ZtoCancelExpressRetObj ret1 = ztoExpressService.cancelExpress("1122365", "12345678", param).getData();
        assertThat(ret1.getStatusCode()).isEqualTo("0000");
        assertThat(ret1.getStatus()).isEqualTo(true);
    }
}
