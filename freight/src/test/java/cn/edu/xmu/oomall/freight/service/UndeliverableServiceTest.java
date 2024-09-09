package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.controller.vo.UndeliverableVo;
import cn.edu.xmu.oomall.freight.dao.UndeliverableDao;
import cn.edu.xmu.oomall.freight.service.dto.UndeliverableDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static cn.edu.xmu.javaee.core.model.Constants.DATE_TIME_FORMATTER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes= FreightApplication.class)
@Transactional
public class UndeliverableServiceTest {

    @Autowired
    private UndeliverableService undeliverableService;

    @MockBean
    RedisUtil redisUtil;

    @Autowired UndeliverableDao undeliverableDao;


    @Test
    public void retrieveUndeliverableRegionsTest01(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        UserDto user = new UserDto();
        user.setName("test1");
        user.setUserLevel(1);

        PageDto<UndeliverableDto> dto = undeliverableService.retrieveUndeliverableRegions(1L, 1, 1, user);
        //assertThat(dto.getList().get(0).getRegionDto()).isNull();
      assertEquals(483250L,dto.getList().get(0).getRegionDto().getId());

    }

    @Test
    public void createUndeliverableTest01(){
        UndeliverableVo undeliverableVo = new UndeliverableVo();
        undeliverableVo.setBeginTime("2022-01-01T12:12:12");
        undeliverableVo.setEndTime("2023-01-01T12:12:12");
        UserDto user = new UserDto();
        user.setName("test1");
        user.setUserLevel(1);
        UndeliverableDto dto = undeliverableService.createUndeliverable(483250L, 2L, undeliverableVo, user);

        LocalDateTime begin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);

        assertEquals("test1",dto.getCreator().getName());
        assertEquals(begin,dto.getBeginTime());
        assertEquals(483250L,dto.getRegionDto().getId());
    }

    @Test
    public void updateUndeliverableTest01(){
        UndeliverableVo undeliverableVo = new UndeliverableVo();
        undeliverableVo.setBeginTime("2022-01-01T12:12:12");
        undeliverableVo.setEndTime("2023-01-01T12:12:12");
        UserDto user = new UserDto();
        user.setName("test1");
        user.setUserLevel(1);
        UndeliverableDto ret = undeliverableService.updateUndeliverable(483250L, 1L, undeliverableVo, user);
        LocalDateTime begin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
        assertEquals(begin,ret.getBeginTime());
        assertEquals("test1",ret.getModifier().getName());
    }

    @Test
    public void delUndeliverableTest01(){
        UserDto user = new UserDto();
        user.setName("test1");
        user.setUserLevel(1);
        undeliverableService.delUndeliverable(483250L, 1L,user);

        assertThrows(BusinessException.class,()->undeliverableDao.findById(1L));
    }




}
