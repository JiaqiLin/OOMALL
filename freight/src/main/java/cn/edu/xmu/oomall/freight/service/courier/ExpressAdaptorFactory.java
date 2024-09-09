package cn.edu.xmu.oomall.freight.service.courier;

import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ExpressAdaptorFactory {
    private static final Logger logger = LoggerFactory.getLogger(ExpressAdaptorFactory.class);

    private ApplicationContext context;

    @Autowired
    public ExpressAdaptorFactory(ApplicationContext context) {
        this.context = context;
    }

    /**
     * 返回商铺的物流渠道服务
     * 简单工厂模式
     * @param shopLogistics 商铺物流渠道
     * @return
     */
    public ExpressAdaptor createExpressAdaptor(ShopLogistics shopLogistics) {
        Logistics logistics = shopLogistics.getLogistics();
        String[] names = context.getBeanNamesForType(ExpressAdaptor.class);
        logger.debug("createExpressAdaptor: names = {}",names);
        return (ExpressAdaptor) context.getBean(logistics.getLogisticsClass());
    }

//    /**
//     * 直接根据物流返回
//     * @param logistics
//     * @return
//     */
//    public ExpressAdaptor createExpressAdaptor(Logistics logistics) {
//        String[] names = context.getBeanNamesForType(ExpressAdaptor.class);
//        logger.debug("createExpressAdaptor: names = {}",names);
//        return (ExpressAdaptor) context.getBean(logistics.getLogisticsClass());
//    }
}
