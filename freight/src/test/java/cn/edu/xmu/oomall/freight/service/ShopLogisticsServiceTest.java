package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.controller.vo.ShopLogisticsVo;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.service.dto.ShopLogisticsDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = FreightApplication.class)
@Transactional
public class ShopLogisticsServiceTest {
    @Autowired
    private ShopLogisticsService shopLogisticsService;

    @MockBean
    RedisUtil redisUtil;

    @Autowired
    private ShopLogisticsDao shopLogisticsDao;

    @Test
    public void createShopLogisticsTest01() {
        ShopLogisticsVo shopLogisticsVo = new ShopLogisticsVo();
        shopLogisticsVo.setLogisticsId(Long.valueOf("2"));
        shopLogisticsVo.setSecret("secret1");
        shopLogisticsVo.setPriority(12);

        UserDto user = new UserDto();
        user.setName("test1");
        user.setId(Long.valueOf("1"));

        ShopLogisticsDto shopLogisticsDto = shopLogisticsService.createShopLogistics(Long.valueOf("2"), user, shopLogisticsVo);
        assertEquals("test1", shopLogisticsDto.getCreator().getName());
        assertEquals(2L, shopLogisticsDto.getLogistics().getId());
        assertEquals("secret1", shopLogisticsDto.getSecret());
    }

    //商铺已存在物流，创建失败
    @Test
    public void createShopLogisticsTest02(){
        ShopLogisticsVo shopLogisticsVo = new ShopLogisticsVo();
        shopLogisticsVo.setLogisticsId(Long.valueOf("1"));
        shopLogisticsVo.setSecret("secret1");
        shopLogisticsVo.setPriority(12);

        UserDto user = new UserDto();
        user.setName("test1");
        user.setId(Long.valueOf("1"));

        assertThrows(BusinessException.class,()->shopLogisticsService.createShopLogistics(1L, user, shopLogisticsVo)) ;
    }

    @Test
    public void retrieveShopLogisticsTest01(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        UserDto user = new UserDto();
        user.setId(Long.valueOf("1"));
        user.setName("test1");

        PageDto<ShopLogisticsDto> dto = shopLogisticsService.retrieveShopLogistics(1L, 1, 1, user);
        System.out.println("dto=====================\t"+dto.getList().get(0));
        assertEquals(3L, dto.getList().get(0).getLogistics().getId());
    }

    @Test
    public void updateShopLogisticsTest01() {
        ShopLogisticsVo shopLogisticsVo = new ShopLogisticsVo();
        shopLogisticsVo.setSecret("secret2");
        shopLogisticsVo.setPriority(10);
        UserDto user = new UserDto();
        user.setId(Long.valueOf("1"));
        user.setName("test1");
        ShopLogisticsDto ret = shopLogisticsService.updateShopLogistics(1L, 1L, user, shopLogisticsVo);
        assertEquals("test1", ret.getModifier().getName());
        assertEquals(10, ret.getPriority());
        assertEquals("secret2", ret.getSecret());
    }

    @Test
    public void suspendShopLogisticsTest01() {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("test2");
        ShopLogisticsDto ret = shopLogisticsService.suspendShopLogistics(1L, 1L, user);
        assertEquals("test2", ret.getModifier().getName());
        assertEquals(ShopLogistics.INVALID, ret.getInvalid());
    }

    @Test
    public void resumeShopLogisticsTest01() {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("test3");
        ShopLogisticsDto ret = shopLogisticsService.resumeShopLogistics(1L, 3L, user);
        assertEquals("test3", ret.getModifier().getName());
        assertEquals(ShopLogistics.VALID, ret.getInvalid());
    }
}
