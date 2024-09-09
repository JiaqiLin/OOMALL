package cn.edu.xmu.oomall.freight.service.courier;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.RegionDao;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.service.courier.dto.CancelExpressAdaptorDto;
import cn.edu.xmu.oomall.freight.service.courier.dto.GetExpressAdaptorDto;
import cn.edu.xmu.oomall.freight.service.courier.dto.PostExpressAdaptorDto;
import cn.edu.xmu.oomall.freight.service.openfeign.ZtoExpressService;
import cn.edu.xmu.oomall.freight.service.openfeign.ZtoParam.ZtoCancelExpressRetObj;
import cn.edu.xmu.oomall.freight.service.openfeign.ZtoParam.ZtoGetExpressRetObj;
import cn.edu.xmu.oomall.freight.service.openfeign.ZtoParam.ZtoPostExpressParam;
import cn.edu.xmu.oomall.freight.service.openfeign.ZtoParam.ZtoPostExpressRetObj;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class ZtoExpressAdaptorTest {
    @Autowired
    private ZtoExpressAdaptor ztoExpressAdaptor;
    @Autowired
    private ShopLogisticsDao shopLogisticsDao;
    @Autowired
    private RegionDao regionDao;
    @MockBean
    private ZtoExpressService ztoExpressService;
    @Test
    public void CreateExpressTest(){
        ZtoPostExpressRetObj ret = JacksonUtil.toObj(
                "{\"message\":\"字符串\",\"statusCode\":\"0000\",\"status\":true,\"result\":{\"billCode\":\"130005102254\"}}", ZtoPostExpressRetObj.class);
        Mockito.when(ztoExpressService.postExpress(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new InternalReturnObject<>(ret));
        Express express = new Express();
        express.setId(Long.valueOf("1"));
        express.setShopLogisticsId(Long.valueOf("1"));
        express.setShopLogisticsDao(shopLogisticsDao);
        express.setRegionDao(regionDao);
        express.setSenderName("张三");
        express.setSenderMobile("13900000000");
        express.setSenderRegionId(Long.valueOf("5"));
        express.setDeliverName("Jone Star");
        express.setDeliverMobile("13500000000");
        express.setDeliverRegionId(Long.valueOf("6"));

        PostExpressAdaptorDto dto = ztoExpressAdaptor.createExpress(express);
        assertThat(dto.getBillCode()).isEqualTo("130005102254");
    }
    @Test
    public void ReturnExpressByBillCodeTest(){
        String s = new StringBuilder()
                .append("{\"message\":\"字符串\",\"statusCode\":\"0000\",\"status\":true,\"result\":[")
                .append("{\"scanType\":\"收件\",\"scanDate\":\"1609297452000\",\"desc\":\"【上海】（021-605511111） 的小吉（18888888888） 已揽收\"}")
                .append("]}")
                .toString();
        ZtoGetExpressRetObj ret = JacksonUtil.toObj(s, ZtoGetExpressRetObj.class);
        Mockito.when(ztoExpressService.getExpressByBillCode(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new InternalReturnObject<>(ret));
        Express express = new Express();
        express.setBillCode("73111390619708");
        express.setShopLogisticsId(Long.valueOf("1"));
        express.setShopLogisticsDao(shopLogisticsDao);
        GetExpressAdaptorDto dto = ztoExpressAdaptor.returnExpressByBillCode(express);
        assertThat(dto.getStatus()).isEqualTo(Byte.valueOf("6"));
        assertThat(dto.getRoutes().get(0).getGmtCreate()).isEqualTo("2020-12-30T11:04:12");
        assertThat(dto.getRoutes().get(0).getContent()).isEqualTo("【上海】（021-605511111） 的小吉（18888888888） 已揽收");
    }

    @Test
    public void CancelExpressTest(){
        ZtoCancelExpressRetObj ret = JacksonUtil.toObj("{\"message\":\"字符串\",\"statusCode\":\"0000\",\"status\":true,\"result\":{}}", ZtoCancelExpressRetObj.class);
        Mockito.when(ztoExpressService.cancelExpress(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new InternalReturnObject<>(ret));
        Express express = new Express();
        express.setBillCode("200824000005397109");
        express.setShopLogisticsId(Long.valueOf("1"));
        express.setShopLogisticsDao(shopLogisticsDao);
        CancelExpressAdaptorDto dto = ztoExpressAdaptor.cancelExpress(express);
        assertThat(dto.getStatus()).isEqualTo(true);
    }
}
