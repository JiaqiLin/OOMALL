package cn.edu.xmu.oomall.freight.service.courier;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.service.courier.dto.CancelExpressAdaptorDto;
import cn.edu.xmu.oomall.freight.service.courier.dto.GetExpressAdaptorDto;
import cn.edu.xmu.oomall.freight.service.courier.dto.PostExpressAdaptorDto;
import cn.edu.xmu.oomall.freight.service.dto.RouteDto;
import cn.edu.xmu.oomall.freight.service.openfeign.SfExpressService;
import cn.edu.xmu.oomall.freight.service.openfeign.SfParam.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


@Service("sfDao")
public class SfExpressAdaptor implements ExpressAdaptor{

    private static final Logger logger= LoggerFactory.getLogger(SfExpressAdaptor.class);

    @Resource
    private SfExpressService sfExpressService;

    /**
     * @description: 创建新订单
     * @param: express
     * @return: cn.edu.xmu.oomall.freight.service.courier.dto.PostExpressAdaptorDto
     * @author
     * @date: 11:01 2022/12/24
     */
    @Override
    public PostExpressAdaptorDto createExpress(Express express) {
        SfPostExpressParam sfPostExpressParam = new SfPostExpressParam();
        // 属性填充

        // 账单信息
        SfPostExpressParam.Order order = sfPostExpressParam.new Order();
        // 交易信息
        SfPostExpressParam.ContactInfo senderInfo = sfPostExpressParam.new ContactInfo();
        SfPostExpressParam.ContactInfo receiverInfo = sfPostExpressParam.new ContactInfo();

        senderInfo.setContactType(1);
        receiverInfo.setContactType(2);

//        SfPostExpressParam.CargoDetail cargoDetail = sfPostExpressParam.new CargoDetail();
        String[] senderRegion = express.getSenderRegion().getMergerName().split(",");
        String[] receiverRegion = express.getDeliverRegion().getMergerName().split(",");


        senderInfo.setContact(express.getSenderName());
        senderInfo.setMobile(express.getSenderMobile());

        receiverInfo.setContact(express.getDeliverName());
        receiverInfo.setMobile(express.getDeliverMobile());

        if(senderRegion.length >= 1) senderInfo.setProvince(senderRegion[0]);
        if(senderRegion.length >= 2) senderInfo.setCity(senderRegion[1]);
        senderInfo.setAddress(express.getSenderAddress());

        if(receiverRegion.length >= 1) receiverInfo.setProvince(receiverRegion[0]);
        if(receiverRegion.length >= 2) receiverInfo.setCity(receiverRegion[1]);
        receiverInfo.setAddress(express.getDeliverAddress());

        List<SfPostExpressParam.ContactInfo> contactInfos = new ArrayList<>();
        contactInfos.add(senderInfo);
        contactInfos.add(receiverInfo);

        order.setOrderId(express.getShopLogistics().getSecret());
        order.setContactInfoList(contactInfos);

        sfPostExpressParam.setMsgData(order);
        sfPostExpressParam.setMsgBody(JacksonUtil.toJson(order));

        Logistics logistics=express.getShopLogistics().getLogistics();
        SfPostExpressRetObj retObj = sfExpressService.postExpress(logistics.getAppId(),logistics.getSecret(),sfPostExpressParam).getData();
        if(!"true".equals(retObj.getSuccess())){
            throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR,retObj.getErrorMsg());
        }
        PostExpressAdaptorDto postExpressAdaptorDto=new PostExpressAdaptorDto();
        postExpressAdaptorDto.setBillCode(retObj.getMsgData().getWaybillNoInfoList().get(0).getWaybillNo());

        return postExpressAdaptorDto;
    }

    @Override
    public GetExpressAdaptorDto returnExpressByBillCode(Express express) {
        SfGetExpressParam sfGetExpressParam = new SfGetExpressParam();
        SfGetExpressParam.MsgData msgData = sfGetExpressParam.new MsgData();
        sfGetExpressParam.setMsgData(msgData);
        List<RouteDto> routes=new ArrayList<>();
        Logistics logistics=express.getShopLogistics().getLogistics();
        SfGetExpressRetObj retObj = sfExpressService.getExpressByBillCode(logistics.getAppId(),logistics.getSecret(),sfGetExpressParam).getData();
        retObj.getMsgData().getRouteResps().get(0).getRoutes().forEach(
                route -> {
                    RouteDto routeDto = new RouteDto();
                    routeDto.setContent(route.getRemark());
                    LocalDateTime gmtCreate = route.getAcceptTime();
                    routeDto.setGmtCreate(gmtCreate);
                    routes.add(routeDto);
                }
        );

        GetExpressAdaptorDto expressAdaptorDto = new GetExpressAdaptorDto();
        expressAdaptorDto.setRoutes(routes);
        expressAdaptorDto.setStatus(retObj.getByteStatus());
        return expressAdaptorDto;
    }

    @Override
    public CancelExpressAdaptorDto cancelExpress(Express express) {
        SfCancelExpressParam sfCancelExpressParam = new SfCancelExpressParam();
        SfCancelExpressParam.WaybillNoInfo waybillNoInfo = sfCancelExpressParam.new WaybillNoInfo();
        SfCancelExpressParam.MsgData msgData = sfCancelExpressParam.new MsgData();
        msgData.setOrderId(express.getId().toString());

        sfCancelExpressParam.setPartnerID(express.getShopLogistics().getSecret());
        sfCancelExpressParam.setMsgData(msgData);

        Logistics logistics=express.getShopLogistics().getLogistics();
        SfCancelExpressRetObj retObj = sfExpressService.cancelExpress(logistics.getAppId(),logistics.getSecret(),sfCancelExpressParam).getData();
        CancelExpressAdaptorDto cancelExpressAdaptorDto=new CancelExpressAdaptorDto();
        if(!"true".equals(retObj.getSuccess())){
            throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR,retObj.getErrorMsg());
        }
        cancelExpressAdaptorDto.setStatus(true);
        return cancelExpressAdaptorDto;
    }
}
