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
import cn.edu.xmu.oomall.freight.service.openfeign.SfExpressService;
import cn.edu.xmu.oomall.freight.service.openfeign.SfParam.SfCancelExpressRetObj;
import cn.edu.xmu.oomall.freight.service.openfeign.SfParam.SfGetExpressRetObj;
import cn.edu.xmu.oomall.freight.service.openfeign.SfParam.SfPostExpressRetObj;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * ClassName SfExpressAdaptorTest
 * Description  TODO
 *
 * @author Mr_X
 * @version 1.0
 * @date 2022/12/24 13:42
 */
@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class SfExpressAdaptorTest {
    @MockBean
    private SfExpressService sfExpressService;
    @Autowired
    private ShopLogisticsDao shopLogisticsDao;
    @Autowired
    private RegionDao regionDao;
    @Autowired
    private SfExpressAdaptor sfExpressAdaptor;

    private static final String SUCCESS_POST_RES_JSON = "{\"success\":\"true\",\"errorCode\":\"S0000\",\"errorMsg\":null," +
            "\"msgData\":{\"orderId\":\"QIAO-20200528-006\",\"waybillNoInfoList\":[{\"waybillType\":1,\"waybillNo\":\"SF7444400043266\"}]}}";

    private static final String EXPRESS_ROUTE_INFO = "{\"success\":\"true\",\"errorCode\":\"S0000\",\"errorMsg\":null," +
            "\"msgData\":{\"routeResps\":[{\"mailNo\":\"SF1011603494291\",\"routes\":[{\"acceptTime\":" +
            "\"2019-05-09 10:11:26\",\"acceptAddress\":\"深圳\",\"opcode\":\"50\",\"remark\":\"已派件\"}," +
            "{\"acceptTime\":\"2019-05-09 18:11:26\",\"acceptAddress\":\"深圳\",\"opcode\":\"80\",\"remark\":\"已签收\"}]}]}}";

    private static final String CANCEL_RESP_INFO = "{\"success\":\"true\",\"errorCode\":\"S0000\",\"errorMsg\":null," +
            "\"msgData\":{\"orderId\":\"eb21c793-a45a-4d1e-9a2e-1b6e0cd49668\",\"waybillNoInfoList\"" +
            ":[{\"waybillType\":1,\"waybillNo\":\"SF7444400043064\"}],\"resStatus\":2,\"extraInfoList\":null}}";

    @Test
    public void createExpressTest(){
        SfPostExpressRetObj ret = JacksonUtil.toObj(SUCCESS_POST_RES_JSON, SfPostExpressRetObj.class);
        System.out.println(ret);
        Mockito.when(sfExpressService.postExpress(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(ret));
        Express express=new Express();
        express.setId((long) 1);
        express.setShopLogisticsId((long)1);
        express.setShopLogisticsDao(shopLogisticsDao);
        express.setRegionDao(regionDao);
        express.setDeliverName("田丽");
        express.setDeliverMobile("13766245825");
        express.setDeliverAddress("站前西路永利酒店斜对面童装店");
        express.setDeliverRegionId((long)1);
        express.setSenderName("小九");
        express.setSenderMobile("15546168286");
        express.setSenderAddress("庆丰三路28号");
        express.setSenderRegionId((long)2);

        PostExpressAdaptorDto dto = sfExpressAdaptor.createExpress(express);
        System.out.println(dto);
        assertThat(dto.getBillCode()).isEqualTo("SF7444400043266");
    }

    @Test
    public void returnExpressByBillCodeTest(){
        SfGetExpressRetObj ret = JacksonUtil.toObj(EXPRESS_ROUTE_INFO, SfGetExpressRetObj.class);
        Mockito.when(sfExpressService.getExpressByBillCode(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(ret));


        Express express=new Express();
        express.setBillCode("SF7444400043266");
        express.setShopLogisticsId(Long.valueOf("1"));
        express.setShopLogisticsDao(shopLogisticsDao);
        GetExpressAdaptorDto dto = sfExpressAdaptor.returnExpressByBillCode(express);
        System.out.println(dto);
        assertThat(dto.getStatus().equals(Express.SIGN_FOR));
    }

    @Test
    public void cancelExpressTest(){
        SfCancelExpressRetObj ret = JacksonUtil.toObj(CANCEL_RESP_INFO, SfCancelExpressRetObj.class);
        Mockito.when(sfExpressService.cancelExpress(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(ret));

        Express express=new Express();
        express.setId((long) 1);
        express.setBillCode("SF7444400043266");
        express.setShopLogisticsId((long)1);
        express.setShopLogisticsDao(shopLogisticsDao);

        CancelExpressAdaptorDto dto = sfExpressAdaptor.cancelExpress(express);
        System.out.println(dto);
        assertThat(dto.getStatus()).isEqualTo(true);
    }
}
