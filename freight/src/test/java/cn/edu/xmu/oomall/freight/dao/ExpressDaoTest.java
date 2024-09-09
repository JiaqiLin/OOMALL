package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class ExpressDaoTest {
    @Autowired
    private ExpressDao expressDao;
    @Test
    public void findByIdTest1(){
        Express express = new Express();
        express.setId(Long.valueOf(797000));
        assertThrows(BusinessException.class, () ->expressDao.findById(Long.valueOf(797000)));
//        assertThat(ret.getId()).isEqualTo(express.getId());
    }

    @Test
    public void findByIdTest2(){
        Express express = new Express();
        express.setId(Long.valueOf(797000));
        assertThrows(BusinessException.class, () ->expressDao.findById(Long.valueOf(797000)));
//        assertThat(ret.getId()).isEqualTo(express.getId());
    }

    @Test
    public void findByIdTest3(){
       expressDao.findById(null);
//        assertThat(ret.getId()).isEqualTo(express.getId());
    }

    @Test
    public void saveByIdTest1(){
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        Express obj = new Express();
        obj.setId(Long.valueOf(797));

        assertThrows(BusinessException.class, () ->expressDao.saveById(obj, user));
    }

    @Test
    public void saveByIdTest2(){
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        expressDao.saveById(null, user);
    }

    @Test
    public void saveByIdTest3(){
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        Express obj = new Express();
        obj.setId(null);
        expressDao.saveById(obj, user);
    }


    @Test
    public void saveByIdTest5(){
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        Express express=new Express();
        express.setShopId(Long.valueOf(1));
        express.setShopLogisticsId((long)1);
        express.setDeliverName("田丽");
        express.setDeliverMobile("13766245825");
        express.setDeliverAddress("站前西路永利酒店斜对面童装店");
        express.setDeliverRegionId((long)1);
        express.setSenderName("小九");
        express.setSenderMobile("15546168286");
        express.setSenderAddress("庆丰三路28号");
        express.setSenderRegionId((long)2);
        expressDao.save(express,user);
        expressDao.saveById(express, user);
    }

    @Test
    public void saveTest(){
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        Express express=new Express();
        express.setShopId(Long.valueOf(1));
        express.setShopLogisticsId((long)1);
        express.setDeliverName("田丽");
        express.setDeliverMobile("13766245825");
        express.setDeliverAddress("站前西路永利酒店斜对面童装店");
        express.setDeliverRegionId((long)1);
        express.setSenderName("小九");
        express.setSenderMobile("15546168286");
        express.setSenderAddress("庆丰三路28号");
        express.setSenderRegionId((long)2);
        expressDao.save(express,user);
        Express ret=expressDao.findById(express.getId());
        assertThat(ret.getId()).isEqualTo(express.getId());
        assertThat(ret.getShopId()).isEqualTo(express.getShopId());
        assertThat(ret.getShopLogisticsId()).isEqualTo(express.getShopLogisticsId());
        assertThat(ret.getSenderName()).isEqualTo(express.getSenderName());
        assertThat(ret.getSenderAddress()).isEqualTo(express.getSenderAddress());
        assertThat(ret.getSenderMobile()).isEqualTo(express.getSenderMobile());
        assertThat(ret.getSenderRegionId()).isEqualTo(express.getSenderRegionId());
        assertThat(ret.getDeliverName()).isEqualTo(express.getDeliverName());
        assertThat(ret.getDeliverAddress()).isEqualTo(express.getDeliverAddress());
        assertThat(ret.getDeliverMobile()).isEqualTo(express.getDeliverMobile());
        assertThat(ret.getDeliverRegionId()).isEqualTo(express.getDeliverRegionId());
        assertThat(ret.getCreatorId()).isEqualTo(user.getId());
        assertThat(ret.getCreatorName()).isEqualTo(user.getName());

    }

    @Test
    public void findByBillCodeTest1(){
        assertThrows(BusinessException.class, () ->expressDao.findByBillCode("11111"));
    }

    @Test
    public void findByBillCodeTest2(){
        expressDao.findByBillCode(null);
    }

    @Test
    public void findByBillCodeTest3(){
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        Express express=new Express();
        express.setShopId(Long.valueOf(1));
        express.setShopLogisticsId((long)1);
        express.setDeliverName("田丽");
        express.setDeliverMobile("13766245825");
        express.setDeliverAddress("站前西路永利酒店斜对面童装店");
        express.setDeliverRegionId((long)1);
        express.setSenderName("小九");
        express.setSenderMobile("15546168286");
        express.setSenderAddress("庆丰三路28号");
        express.setSenderRegionId((long)2);
        expressDao.save(express,user);
        express.setBillCode("UT0000498364212");
        expressDao.saveById(express, user);
        Express ret=expressDao.findByBillCode("UT0000498364212");
        assertThat(ret.getBillCode()).isEqualTo("UT0000498364212");
    }


}
