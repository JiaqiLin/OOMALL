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
import cn.edu.xmu.oomall.freight.service.openfeign.ZtoExpressService;
import cn.edu.xmu.oomall.freight.service.openfeign.ZtoParam.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service("ztoDao")
public class ZtoExpressAdaptor implements ExpressAdaptor{
    private static final Logger logger= LoggerFactory.getLogger(ZtoExpressAdaptor.class);

    @Resource
    private ZtoExpressService ztoExpressService;

    /*@Autowired
    public ZtoExpressAdaptor(ZtoExpressService ztoExpressService){
        this.ztoExpressService=ztoExpressService;
    }*/

    @Override
    public PostExpressAdaptorDto createExpress(Express express) {
        ZtoPostExpressParam ztoPostExpressParam = new ZtoPostExpressParam();
        ZtoPostExpressParam.Body body = ztoPostExpressParam.new Body();
        body.setPartnerOrderCode(express.getShopLogistics().getSecret());
        ZtoPostExpressParam.AccountDto accountInfo = ztoPostExpressParam.new AccountDto();
        body.setAccountInfo(accountInfo);

        ZtoPostExpressParam.SenderInfoInput senderInfo = ztoPostExpressParam.new SenderInfoInput();
        senderInfo.setSenderMobile(express.getSenderMobile());
        senderInfo.setSenderName(express.getSenderName());
        String[] senderRegion = express.getSenderRegion().getMergerName().split(",");
        if(senderRegion.length >= 1) senderInfo.setSenderProvince(senderRegion[0]);
        if(senderRegion.length >= 2) senderInfo.setSenderCity(senderRegion[1]);
        if(senderRegion.length >= 3) senderInfo.setSenderDistrict(senderRegion[2]);
        if(senderRegion.length >= 4) senderInfo.setSenderAddress(senderRegion[3]);
        body.setSenderInfo(senderInfo);

        ZtoPostExpressParam.ReceiveInfoInput receiveInfo = ztoPostExpressParam.new ReceiveInfoInput();
        receiveInfo.setReceiverMobile(express.getDeliverMobile());
        receiveInfo.setReceiverName(express.getDeliverName());
        String[] receiverRegion = express.getDeliverRegion().getMergerName().split(",");
        if(receiverRegion.length >= 1) receiveInfo.setReceiverProvince(receiverRegion[0]);
        if(receiverRegion.length >= 2) receiveInfo.setReceiverCity(receiverRegion[1]);
        if(receiverRegion.length >= 3) receiveInfo.setReceiverDistrict(receiverRegion[2]);
        if(receiverRegion.length >= 4) receiveInfo.setReceiverAddress(receiverRegion[3]);
        body.setReceiveInfo(receiveInfo);

        ztoPostExpressParam.setBody(JacksonUtil.toJson(body));

        Logistics logistics = express.getShopLogistics().getLogistics();
        ZtoPostExpressRetObj ztoPostExpressRetObj = ztoExpressService.postExpress(logistics.getAppId(), logistics.getSecret(), ztoPostExpressParam).getData();
        if(!ztoPostExpressRetObj.getStatusCode().equals("0000")){
            throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR,ztoPostExpressRetObj.getMessage());
        }
        PostExpressAdaptorDto postExpressAdaptorDto = new PostExpressAdaptorDto();
        postExpressAdaptorDto.setBillCode(ztoPostExpressRetObj.getResult().getBillCode());
        return postExpressAdaptorDto;
    }

    @Override
    public GetExpressAdaptorDto returnExpressByBillCode(Express express) {
        ZtoGetExpressParam ztoGetExpressParam = new ZtoGetExpressParam();
        ZtoGetExpressParam.Body body = ztoGetExpressParam.new Body();
        body.setBillCode(express.getBillCode());
        ztoGetExpressParam.setBody(JacksonUtil.toJson(body));
        List<RouteDto> routes = new ArrayList<>();
        Logistics logistics = express.getShopLogistics().getLogistics();
        ZtoGetExpressRetObj ztoGetExpressRetObj = ztoExpressService.getExpressByBillCode(logistics.getAppId(), logistics.getSecret(), ztoGetExpressParam).getData();
        ztoGetExpressRetObj.getResult().stream().forEach(result -> {RouteDto route = new RouteDto();
                                                                route.setContent(result.getDesc());
                                                                route.setGmtCreate(result.getScanDate());
                                                                routes.add(route);});
        GetExpressAdaptorDto getExpressAdaptorDto = new GetExpressAdaptorDto();
        getExpressAdaptorDto.setRoutes(routes);
        getExpressAdaptorDto.setStatus(ztoGetExpressRetObj.getByteStatus());
        return getExpressAdaptorDto;
    }

    @Override
    public CancelExpressAdaptorDto cancelExpress(Express express) {
        ZtoCancelExpressParam ztoCancelExpressParam = new ZtoCancelExpressParam();
        ZtoCancelExpressParam.Body body = ztoCancelExpressParam.new Body();
        body.setBillCode(express.getBillCode());
        ztoCancelExpressParam.setBody(JacksonUtil.toJson(body));
        Logistics logistics = express.getShopLogistics().getLogistics();
        ZtoCancelExpressRetObj ztoCancelExpressRetObj = ztoExpressService.cancelExpress(logistics.getAppId(), logistics.getSecret(), ztoCancelExpressParam).getData();

        CancelExpressAdaptorDto cancelExpressAdaptorDto = new CancelExpressAdaptorDto();
        if(!ztoCancelExpressRetObj.getStatusCode().equals("0000")){
            throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR, ztoCancelExpressRetObj.getMessage());
        }
        cancelExpressAdaptorDto.setStatus(true);
        return cancelExpressAdaptorDto;
    }


}
