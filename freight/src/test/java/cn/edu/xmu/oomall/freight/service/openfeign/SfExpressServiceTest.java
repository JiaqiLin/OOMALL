package cn.edu.xmu.oomall.freight.service.openfeign;

import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.service.openfeign.SfParam.SfCancelExpressParam;
import cn.edu.xmu.oomall.freight.service.openfeign.SfParam.SfGetExpressParam;
import cn.edu.xmu.oomall.freight.service.openfeign.SfParam.SfPostExpressParam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassName SfExpressServiceTest
 * Description  TODO
 *
 * @author Mr_X
 * @version 1.0
 * @date 2022/12/24 13:48
 */
@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class SfExpressServiceTest {
    @MockBean
    private SfExpressService sfExpressService;

    @Test
    public void postExpressTest(){
        SfPostExpressParam param = new SfPostExpressParam();
//        System.out.println(sfExpressService.postExpress(param));
    }
    @Test
    public void getExpressTest(){
        SfGetExpressParam param = new SfGetExpressParam();
//        System.out.println(sfExpressService.getExpressByBillCode(param));
    }
    @Test
    public void cancelExpressTest(){
        SfCancelExpressParam param = new SfCancelExpressParam();
//        System.out.println(sfExpressService.cancelExpress(param));
    }
}
