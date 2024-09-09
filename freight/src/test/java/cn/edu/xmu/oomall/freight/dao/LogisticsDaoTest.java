package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class LogisticsDaoTest {
    @Autowired
    private LogisticsDao logisticsDao;
    @MockBean
    private RedisUtil redisUtil;

    final String KEY="L%d";

    /*
    查找存在的logistics
     */
    @Test
    public void findByIdTest1(){
        Logistics logistics = new Logistics();
        logistics.setId(1L);
        logistics.setName("顺丰快递");
        logistics.setAppId("SF1001");
        logistics.setSnPattern("^SF[A-Za-z0-9-]{4,35}$");
        logistics.setLogisticsClass("sfDao");

        Logistics ret = logisticsDao.findById(Long.valueOf(1));

        assertThat(ret.getId()).isEqualTo(logistics.getId());
        assertThat(ret.getName()).isEqualTo(logistics.getName());
        assertThat(ret.getAppId()).isEqualTo(logistics.getAppId());
        assertThat(ret.getSnPattern()).isEqualTo(logistics.getSnPattern());
        assertThat(ret.getLogisticsClass()).isEqualTo(logistics.getLogisticsClass());
    }

    /*
    查找不存在的logistics,报错
     */
    @Test
    public void findByIdTest2(){

        assertThrows(BusinessException.class, () ->logisticsDao.findById(4L));

    }


    /**
     * 查找所有的logistics
     */
    @Test
    public void retrieveAllTest(){
        List<Logistics> ret= logisticsDao.retrieveAll();
        System.out.println(ret);
    }

    /**
     * redis命中情况
     */
    @Test
    public void findbyIdTest3(){
        Logistics logistics = new Logistics();
        logistics.setId(1L);
        logistics.setName("顺丰快递");
        logistics.setAppId("SF1001");
        logistics.setSnPattern("^SF[A-Za-z0-9-]{4,35}$");

        Mockito.when(redisUtil.hasKey((String.format(KEY,1)))).thenReturn(true);
        Mockito.when(redisUtil.get(String.format(KEY,1))).thenReturn(logistics);

        Logistics ret = logisticsDao.findById(Long.valueOf(1));

        assertThat(ret.getId()).isEqualTo(logistics.getId());
        assertThat(ret.getName()).isEqualTo(logistics.getName());
        assertThat(ret.getAppId()).isEqualTo(logistics.getAppId());
        assertThat(ret.getSnPattern()).isEqualTo(logistics.getSnPattern());
    }




}

