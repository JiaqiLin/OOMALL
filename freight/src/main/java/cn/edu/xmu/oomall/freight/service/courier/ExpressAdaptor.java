package cn.edu.xmu.oomall.freight.service.courier;

import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.service.courier.dto.CancelExpressAdaptorDto;
import cn.edu.xmu.oomall.freight.service.courier.dto.GetExpressAdaptorDto;
import cn.edu.xmu.oomall.freight.service.courier.dto.PostExpressAdaptorDto;

public interface ExpressAdaptor {
    //TODO 各物流统一格式的返回信息Dto
    /**
     * 创建运单
     */
    PostExpressAdaptorDto createExpress(Express express);

    /**
     * 向第三方平台查询运单
     * */
    GetExpressAdaptorDto returnExpressByBillCode(Express express);


    /**
     * 取消订单
     * */
    CancelExpressAdaptorDto cancelExpress(Express express);
}
