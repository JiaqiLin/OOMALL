package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.dao.bo.Region;
import cn.edu.xmu.oomall.freight.dao.bo.Undeliverable;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import com.github.pagehelper.PageInfo;
import com.mysql.cj.result.LocalDateTimeValueFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

import static cn.edu.xmu.javaee.core.model.Constants.DATE_TIME_FORMATTER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class UndeliverableDaoTest {

    @Autowired
    private UndeliverableDao undeliverableDao;

    @Test
    public void findByIdTest1(){
        Region region = new Region();
        region.setId(483250L);
        Undeliverable undeliverable = new Undeliverable();
        undeliverable.setRegion(region);
        undeliverable.setShopLogisticsId(1L);

        Undeliverable ret = undeliverableDao.findById(1L);
        //System.out.println(ret);

        assertThat(ret.getRegion().getId()).isEqualTo(undeliverable.getRegion().getId());

    }

    @Test
    public void findByRegionIdAndShopLogisticsIdTest(){
        Region region = new Region();
        region.setId(483250L);
        Undeliverable undeliverable = new Undeliverable();
        undeliverable.setRegion(region);
        undeliverable.setShopLogisticsId(1L);

        Undeliverable ret = undeliverableDao.findByRegionIdAndShopLogisticsId(483250L,1L);
        System.out.println(ret);

        assertThat(ret.getRegion().getId()).isEqualTo(undeliverable.getRegion().getId());
    }

    @Test
    public void saveTest(){
        UserDto user = new UserDto();
        user.setName("test1");
        user.setUserLevel(1);

        Undeliverable undeliverable=new Undeliverable();
        LocalDateTime begin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse("2023-01-01T12:12:12", DATE_TIME_FORMATTER);
        undeliverable.setBeginTime(begin);
        undeliverable.setEndTime(end);
        undeliverable.setShopLogisticsId(2L);
        undeliverable.setRegionId(483250L);
        undeliverableDao.save(undeliverable,user);

        assertThat(undeliverable.getId()).isNotNull();
    }

    @Test
    public void saveByIdTest(){
        Undeliverable undeliverable = undeliverableDao.findById(1L);
        undeliverable.setShopLogisticsId(2L);
        UserDto user = new UserDto();
        user.setName("test1");
        user.setUserLevel(1);

        undeliverableDao.saveById(undeliverable,user);

        Undeliverable ret = undeliverableDao.findById(1L);

        assertThat(ret.getShopLogisticsId()).isEqualTo(2L);

    }

    @Test
    public void delByIdTest(){
        Undeliverable undeliverable = undeliverableDao.findById(1L);
        undeliverableDao.delById(undeliverable);
        assertThrows(BusinessException.class,()->undeliverableDao.findById(1L)) ;
    }

    @Test
    public void retrieveByShopLogisticsIdTest(){
        List<Undeliverable> list = undeliverableDao.retrieveByShopLogisticsId(1L, 1, 1);
        Undeliverable ret = list.get(0);
        assertThat(ret.getShopLogisticsId()).isEqualTo(1L);
        assertThat(ret.getRegion().getId()).isEqualTo(483250L);

    }


}
