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
import cn.edu.xmu.oomall.freight.service.openfeign.JtExpressService;
import cn.edu.xmu.oomall.freight.service.openfeign.JtParam.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service("jtDao")
public class JtExpressAdaptor implements ExpressAdaptor{
    private static final Logger logger= LoggerFactory.getLogger(JtExpressAdaptor.class);


    @Resource
    private JtExpressService jtExpressService;
//    @Autowired
//    public JtExpressAdaptor(JtExpressService jtExpressService){
//        this.jtExpressService=jtExpressService;
//    }
    @Override
    public PostExpressAdaptorDto createExpress(Express express) {
        JtPostExpressParam jtPostExpressParam=new JtPostExpressParam();
        JtPostExpressParam.BizContent bizContent=jtPostExpressParam.new BizContent();
        bizContent.setCustomerCode(express.getShopLogistics().getSecret());
        bizContent.setTxlogisticId(express.getId().toString());

        JtPostExpressParam.Consignee sender=jtPostExpressParam.new Consignee();
        sender.setName(express.getSenderName());
        sender.setMobile(express.getSenderMobile());
        String [] senderRegion=express.getSenderRegion().getMergerName().split(",");
        if(senderRegion.length>=1) sender.setProv(senderRegion[0]);
        if(senderRegion.length>=2) sender.setCity(senderRegion[1]);
        if(senderRegion.length>=3) sender.setArea(senderRegion[2]);
        sender.setAddress(express.getSenderAddress());
        bizContent.setSender(sender);

        JtPostExpressParam.Consignee receiver=jtPostExpressParam.new Consignee();
        receiver.setName(express.getDeliverName());
        receiver.setMobile(express.getDeliverMobile());
        String [] receiverRegion=express.getDeliverRegion().getMergerName().split(",");
        if(receiverRegion.length>=1) receiver.setProv(senderRegion[0]);
        if(receiverRegion.length>=2) receiver.setCity(senderRegion[1]);
        if(receiverRegion.length>=3) receiver.setArea(senderRegion[2]);
        receiver.setAddress(express.getDeliverAddress());
        bizContent.setReceiver(receiver);

        jtPostExpressParam.setBizContent(JacksonUtil.toJson(bizContent));

//        MultiValueMap<String, String> headers;
//        headers.add("apiAccount","");
//        headers.add("digest");
//        headers.add("timestamp",);
        Logistics logistics=express.getShopLogistics().getLogistics();
        JtPostExpressRetObj jtPostExpressRetObj= jtExpressService.postExpress(logistics.getAppId(),logistics.getSecret(),System.currentTimeMillis(),jtPostExpressParam).getData();
        if(!jtPostExpressRetObj.getCode().equals("1")){
            throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR,jtPostExpressRetObj.getMsg());
        }
        PostExpressAdaptorDto postExpressAdaptorDto=new PostExpressAdaptorDto();
        postExpressAdaptorDto.setBillCode(jtPostExpressRetObj.getData().getBillCode());
        return postExpressAdaptorDto;
    }

    @Override
    public GetExpressAdaptorDto returnExpressByBillCode(Express express) {
        JtGetExpressParam jtGetExpressParam=new JtGetExpressParam();
        JtGetExpressParam.BizContent bizContent=jtGetExpressParam.new BizContent();
        bizContent.setBillCodes(express.getBillCode());
        jtGetExpressParam.setBizContent(JacksonUtil.toJson(bizContent));
        List<RouteDto> routes=new ArrayList<>();
        Logistics logistics=express.getShopLogistics().getLogistics();
        JtGetExpressRetObj jtGetExpressRetObj=jtExpressService.getExpressByBillCode(logistics.getAppId(),logistics.getSecret(),System.currentTimeMillis(),jtGetExpressParam).getData();
        if(!jtGetExpressRetObj.getCode().equals("1")){
            throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR,jtGetExpressRetObj.getMsg());
        }
        jtGetExpressRetObj.getData().get(0).getDetails().stream().forEach(detail ->  { RouteDto route=new RouteDto();
                                                                                route.setContent(detail.getDesc());
                                                                                route.setGmtCreate(detail.getScanTime());
                                                                                routes.add(route);});
        GetExpressAdaptorDto getExpressAdaptorDto=new GetExpressAdaptorDto();
        getExpressAdaptorDto.setRoutes(routes);
        getExpressAdaptorDto.setStatus(jtGetExpressRetObj.getByteStatus());
        return getExpressAdaptorDto;
    }

    @Override
    public CancelExpressAdaptorDto cancelExpress(Express express) {
        JtCancelExpressParam jtCancelExpressParam=new JtCancelExpressParam();
        JtCancelExpressParam.BizContent bizContent=jtCancelExpressParam.new BizContent();
        bizContent.setCustomerCode(express.getShopLogistics().getSecret());
        bizContent.setTxlogisticId(express.getId().toString());
        jtCancelExpressParam.setBizContent(JacksonUtil.toJson(bizContent));
        Logistics logistics=express.getShopLogistics().getLogistics();
        JtCancelExpressRetObj jtCancelExpressRetObj=jtExpressService.cancelExpress(logistics.getAppId(),logistics.getSecret(),System.currentTimeMillis(),jtCancelExpressParam).getData();

        CancelExpressAdaptorDto cancelExpressAdaptorDto=new CancelExpressAdaptorDto();
        if(!jtCancelExpressRetObj.getCode().equals("1")){
            throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR,jtCancelExpressRetObj.getMsg());
        }
        cancelExpressAdaptorDto.setStatus(true);
        return cancelExpressAdaptorDto;
    }
}
