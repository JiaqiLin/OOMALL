package cn.edu.xmu.oomall.freight.service;


import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.service.dto.SimpleExpressDto;
import cn.edu.xmu.oomall.freight.service.openfeign.JtExpressService;
import cn.edu.xmu.oomall.freight.service.openfeign.JtParam.JtPostExpressRetObj;
import cn.edu.xmu.oomall.freight.service.openfeign.SfExpressService;
import cn.edu.xmu.oomall.freight.service.openfeign.SfParam.SfPostExpressRetObj;
import cn.edu.xmu.oomall.freight.service.openfeign.ZtoExpressService;
import cn.edu.xmu.oomall.freight.service.openfeign.ZtoParam.ZtoPostExpressRetObj;
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
public class ExpressService1Test {
    @Autowired
    private ExpressService expressService;

    @MockBean
    private JtExpressService jtExpressService;
    @MockBean
    private SfExpressService sfExpressService;
    @MockBean
    private ZtoExpressService ztoExpressService;


    /**
     * 最佳匹配成功
     */
    @Test
    public void createExpressTest1(){
        SfPostExpressRetObj sfRet = JacksonUtil.toObj("{\"success\":\"true\",\"errorCode\":\"S0000\",\"errorMsg\":null," +
                "\"msgData\":{\"orderId\":\"QIAO-20200528-006\",\"waybillNoInfoList\":[{\"waybillType\":1,\"waybillNo\":\"SF7444400043266\"}]}}", SfPostExpressRetObj.class);
        Mockito.when(sfExpressService.postExpress(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(sfRet));
        ZtoPostExpressRetObj ztoRet = JacksonUtil.toObj(
                "{\"message\":\"字符串\",\"statusCode\":\"0000\",\"status\":true,\"result\":{\"billCode\":\"130005102254\"}}", ZtoPostExpressRetObj.class);
        Mockito.when(ztoExpressService.postExpress(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new InternalReturnObject<>(ztoRet));
        JtPostExpressRetObj jtRet= JacksonUtil.toObj(
                "{\"code\":\"1\",\"msg\":\"success\",\"data\":{\"txlogisticId\":\"TEST20220704210006\",\"billCode\":\"UT0000498364212\",\"sortingCode\":\"382 300-64 010\",\"sumFreight\":\"5.00\" ,\"createOrderTime\":\"2022-07-04 12:00:53\",\"lastCenterName\":\"华东转运中心B1\"}}",JtPostExpressRetObj.class);
        Mockito.when(jtExpressService.postExpress(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(jtRet));
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        Express express=new Express();
        express.setShopId(Long.valueOf("1"));
        express.setDeliverName("田丽");
        express.setDeliverMobile("13766245825");
        express.setDeliverAddress("站前西路永利酒店斜对面童装店");
        express.setDeliverRegionId(Long.valueOf("1"));
        SimpleExpressDto dto= expressService.createExpressByPriority(Long.valueOf("1"),express,user);
        assertThat(dto.getBillCode()).isEqualTo("SF7444400043266");
    }


    /**
     * 最佳匹配失败
     */
    @Test
    public void createExpressTest2(){
        SfPostExpressRetObj sfRet = JacksonUtil.toObj("{\"success\":\"true\",\"errorCode\":\"S0000\",\"errorMsg\":null," +
                "\"msgData\":{\"orderId\":\"QIAO-20200528-006\",\"waybillNoInfoList\":[{\"waybillType\":1,\"waybillNo\":\"SF7444400043266\"}]}}", SfPostExpressRetObj.class);
        Mockito.when(sfExpressService.postExpress(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(sfRet));
        ZtoPostExpressRetObj ztoRet = JacksonUtil.toObj(
                "{\"message\":\"字符串\",\"statusCode\":\"0000\",\"status\":true,\"result\":{\"billCode\":\"130005102254\"}}", ZtoPostExpressRetObj.class);
        Mockito.when(ztoExpressService.postExpress(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new InternalReturnObject<>(ztoRet));
        JtPostExpressRetObj jtRet= JacksonUtil.toObj(
                "{\"code\":\"1\",\"msg\":\"success\",\"data\":{\"txlogisticId\":\"TEST20220704210006\",\"billCode\":\"UT0000498364212\",\"sortingCode\":\"382 300-64 010\",\"sumFreight\":\"5.00\" ,\"createOrderTime\":\"2022-07-04 12:00:53\",\"lastCenterName\":\"华东转运中心B1\"}}",JtPostExpressRetObj.class);
        Mockito.when(jtExpressService.postExpress(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(jtRet));
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        Express express=new Express();
        express.setDeliverName("田丽");
        express.setDeliverMobile("13766245825");
        express.setDeliverAddress("站前西路永利酒店斜对面童装店");
        express.setDeliverRegionId(Long.valueOf("6666"));
        assertThrows(BusinessException.class,() ->expressService.createExpressByPriority(Long.valueOf("15"),express,user));
    }


    /**
     * 确定物流，商铺物流无效
     */
    @Test
    public void createExpressTest3(){
        SfPostExpressRetObj sfRet = JacksonUtil.toObj("{\"success\":\"true\",\"errorCode\":\"S0000\",\"errorMsg\":null," +
                "\"msgData\":{\"orderId\":\"QIAO-20200528-006\",\"waybillNoInfoList\":[{\"waybillType\":1,\"waybillNo\":\"SF7444400043266\"}]}}", SfPostExpressRetObj.class);
        Mockito.when(sfExpressService.postExpress(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(sfRet));
        ZtoPostExpressRetObj ztoRet = JacksonUtil.toObj(
                "{\"message\":\"字符串\",\"statusCode\":\"0000\",\"status\":true,\"result\":{\"billCode\":\"130005102254\"}}", ZtoPostExpressRetObj.class);
        Mockito.when(ztoExpressService.postExpress(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new InternalReturnObject<>(ztoRet));
        JtPostExpressRetObj jtRet= JacksonUtil.toObj(
                "{\"code\":\"1\",\"msg\":\"success\",\"data\":{\"txlogisticId\":\"TEST20220704210006\",\"billCode\":\"UT0000498364212\",\"sortingCode\":\"382 300-64 010\",\"sumFreight\":\"5.00\" ,\"createOrderTime\":\"2022-07-04 12:00:53\",\"lastCenterName\":\"华东转运中心B1\"}}",JtPostExpressRetObj.class);
        Mockito.when(jtExpressService.postExpress(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(jtRet));
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        Express express=new Express();
        express.setShopLogisticsId(Long.valueOf("3"));
        express.setDeliverName("田丽");
        express.setDeliverMobile("13766245825");
        express.setDeliverAddress("站前西路永利酒店斜对面童装店");
        express.setDeliverRegionId(Long.valueOf("1"));
        express.setSenderName("小九");
        express.setSenderMobile("15546168286");
        express.setSenderAddress("庆丰三路28号");
        express.setSenderRegionId((long)2);
        assertThrows(BusinessException.class,() ->expressService.createExpress(Long.valueOf("1"),express,user));
//        assertThat(dto.getBillCode()).isEqualTo("SF7444400043266");
//        assertThrows(BusinessException.class,() ->expressService.createExpress(Long.valueOf("2"),express,user));
    }

    /**
     * 确定物流成功
     */
    @Test
    public void createExpressTest4(){
        SfPostExpressRetObj sfRet = JacksonUtil.toObj("{\"success\":\"true\",\"errorCode\":\"S0000\",\"errorMsg\":null," +
                "\"msgData\":{\"orderId\":\"QIAO-20200528-006\",\"waybillNoInfoList\":[{\"waybillType\":1,\"waybillNo\":\"SF7444400043266\"}]}}", SfPostExpressRetObj.class);
        Mockito.when(sfExpressService.postExpress(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(sfRet));
        ZtoPostExpressRetObj ztoRet = JacksonUtil.toObj(
                "{\"message\":\"字符串\",\"statusCode\":\"0000\",\"status\":true,\"result\":{\"billCode\":\"130005102254\"}}", ZtoPostExpressRetObj.class);
        Mockito.when(ztoExpressService.postExpress(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new InternalReturnObject<>(ztoRet));
        JtPostExpressRetObj jtRet= JacksonUtil.toObj(
                "{\"code\":\"1\",\"msg\":\"success\",\"data\":{\"txlogisticId\":\"TEST20220704210006\",\"billCode\":\"UT0000498364212\",\"sortingCode\":\"382 300-64 010\",\"sumFreight\":\"5.00\" ,\"createOrderTime\":\"2022-07-04 12:00:53\",\"lastCenterName\":\"华东转运中心B1\"}}",JtPostExpressRetObj.class);
        Mockito.when(jtExpressService.postExpress(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(jtRet));
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        Express express=new Express();
        express.setShopLogisticsId(Long.valueOf("6"));
        express.setDeliverName("田丽");
        express.setDeliverMobile("13766245825");
        express.setDeliverAddress("站前西路永利酒店斜对面童装店");
        express.setDeliverRegionId(Long.valueOf("1"));
        express.setSenderName("小九");
        express.setSenderMobile("15546168286");
        express.setSenderAddress("庆丰三路28号");
        express.setSenderRegionId((long)2);
        SimpleExpressDto dto=expressService.createExpress(Long.valueOf("2"),express,user);
        assertThat(dto.getBillCode()).isEqualTo("UT0000498364212");
    }

    /**
     * 确定物流，物流不可达收件地区
     */
    @Test
    public void createExpressTest5(){
        SfPostExpressRetObj sfRet = JacksonUtil.toObj("{\"success\":\"true\",\"errorCode\":\"S0000\",\"errorMsg\":null," +
                "\"msgData\":{\"orderId\":\"QIAO-20200528-006\",\"waybillNoInfoList\":[{\"waybillType\":1,\"waybillNo\":\"SF7444400043266\"}]}}", SfPostExpressRetObj.class);
        Mockito.when(sfExpressService.postExpress(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(sfRet));
        ZtoPostExpressRetObj ztoRet = JacksonUtil.toObj(
                "{\"message\":\"字符串\",\"statusCode\":\"0000\",\"status\":true,\"result\":{\"billCode\":\"130005102254\"}}", ZtoPostExpressRetObj.class);
        Mockito.when(ztoExpressService.postExpress(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new InternalReturnObject<>(ztoRet));
        JtPostExpressRetObj jtRet= JacksonUtil.toObj(
                "{\"code\":\"1\",\"msg\":\"success\",\"data\":{\"txlogisticId\":\"TEST20220704210006\",\"billCode\":\"UT0000498364212\",\"sortingCode\":\"382 300-64 010\",\"sumFreight\":\"5.00\" ,\"createOrderTime\":\"2022-07-04 12:00:53\",\"lastCenterName\":\"华东转运中心B1\"}}",JtPostExpressRetObj.class);
        Mockito.when(jtExpressService.postExpress(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(jtRet));
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        Express express=new Express();
        express.setShopLogisticsId(Long.valueOf("1"));
        express.setDeliverName("田丽");
        express.setDeliverMobile("13766245825");
        express.setDeliverAddress("站前西路永利酒店斜对面童装店");
        express.setDeliverRegionId(Long.valueOf("483250"));
        express.setSenderName("小九");
        express.setSenderMobile("15546168286");
        express.setSenderAddress("庆丰三路28号");
        express.setSenderRegionId(Long.valueOf("2"));
        assertThrows(BusinessException.class,() ->expressService.createExpress(Long.valueOf("1"),express,user));
    }

    /**
     * 确定物流，商铺物流不为对应商户
     */
    @Test
    public void createExpressTest6(){
        SfPostExpressRetObj sfRet = JacksonUtil.toObj("{\"success\":\"true\",\"errorCode\":\"S0000\",\"errorMsg\":null," +
                "\"msgData\":{\"orderId\":\"QIAO-20200528-006\",\"waybillNoInfoList\":[{\"waybillType\":1,\"waybillNo\":\"SF7444400043266\"}]}}", SfPostExpressRetObj.class);
        Mockito.when(sfExpressService.postExpress(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(sfRet));
        ZtoPostExpressRetObj ztoRet = JacksonUtil.toObj(
                "{\"message\":\"字符串\",\"statusCode\":\"0000\",\"status\":true,\"result\":{\"billCode\":\"130005102254\"}}", ZtoPostExpressRetObj.class);
        Mockito.when(ztoExpressService.postExpress(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new InternalReturnObject<>(ztoRet));
        JtPostExpressRetObj jtRet= JacksonUtil.toObj(
                "{\"code\":\"1\",\"msg\":\"success\",\"data\":{\"txlogisticId\":\"TEST20220704210006\",\"billCode\":\"UT0000498364212\",\"sortingCode\":\"382 300-64 010\",\"sumFreight\":\"5.00\" ,\"createOrderTime\":\"2022-07-04 12:00:53\",\"lastCenterName\":\"华东转运中心B1\"}}",JtPostExpressRetObj.class);
        Mockito.when(jtExpressService.postExpress(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new InternalReturnObject<>(jtRet));
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        Express express=new Express();
        express.setShopLogisticsId(Long.valueOf("4"));
        express.setDeliverName("田丽");
        express.setDeliverMobile("13766245825");
        express.setDeliverAddress("站前西路永利酒店斜对面童装店");
        express.setDeliverRegionId(Long.valueOf("1"));
        express.setSenderName("小九");
        express.setSenderMobile("15546168286");
        express.setSenderAddress("庆丰三路28号");
        express.setSenderRegionId((long)2);
        assertThrows(BusinessException.class,() ->expressService.createExpress(Long.valueOf("1"),express,user));
    }
}
