package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = FreightApplication.class)
public class LogisticsServiceTest {

    @Autowired
    LogisticsService logisticsService;

    /**
     * 根据快递单号查找快递公司:找到
     */
    @Test
    public void findLogisticsByBillCodeTest1(){
        Logistics ret= logisticsService.findLogisticsByBillCode("JT1728392817277");
        Logistics logistics = new Logistics();
        logistics.setName("极兔速递");
        assertThat(ret.getName()).isEqualTo(logistics.getName());
    }

    /**
     * 根据快递单号查找快递公司:订单号不合法不属于任何一家快递公司
     */
    @Test
    public void findLogisticsByBillCodeTest2(){
        assertThrows(BusinessException.class,()->logisticsService.findLogisticsByBillCode("JT172839281"));

    }
}
