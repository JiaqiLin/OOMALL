//package cn.edu.xmu.oomall.order.controller;
//
//import cn.edu.xmu.javaee.core.model.ReturnNo;
//import cn.edu.xmu.javaee.core.model.dto.PageDto;
//import cn.edu.xmu.javaee.core.model.dto.UserDto;
//import cn.edu.xmu.javaee.core.util.JwtHelper;
//import cn.edu.xmu.javaee.core.util.RedisUtil;
//import cn.edu.xmu.oomall.order.OrderTestApplication;
//import cn.edu.xmu.oomall.order.service.OrderService;
//import cn.edu.xmu.oomall.order.service.dto.*;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.hamcrest.CoreMatchers.is;
//
//@SpringBootTest(classes = OrderTestApplication.class)
//@Transactional
//@AutoConfigureMockMvc
//public class ShopOwnerControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    private static String adminToken;
//
//    @MockBean
//    private RedisUtil redisUtil;
//
//    @BeforeAll
//    public static void setup(){
//        JwtHelper jwtHelper = new JwtHelper();
//        adminToken = jwtHelper.createToken(2L, "13088admin", 0L, 1, 3600);
//    }
//
//    @Test
//    public void retrieveOrderTest1() throws Exception{
//        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/{shopId}/orders/{id}", 1L, 2L)
//                        .header("authorization", adminToken)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderItems[0].quantity", is(3)))
//                .andDo(MockMvcResultHandlers.print());
//    }
//
//    @Test
//    public void retrieveOrdersTest() throws Exception{
//        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/{shopId}/orders", 1L)
//                        .header("authorization",adminToken)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.page", is(1)))
//                .andDo(MockMvcResultHandlers.print());
//    }
//
//    @Test
//    public void updateOrderMessage() throws Exception{
//        String body = "{\"message\": \"test\"}";
//        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/orders/{id}",1L, 2L)
//                        .header("authorization", adminToken)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(body.getBytes("utf-8")))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
//                .andDo(MockMvcResultHandlers.print());
//    }
//
//    @Test
//    public void delOrdersTest01() throws Exception {
////        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
//
//        this.mockMvc.perform(MockMvcRequestBuilders.delete("/shops/{shopId}/orders/{id}", 1L,1L)
//                        .header("authorization", adminToken)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
//                .andDo(MockMvcResultHandlers.print());
//    }
//
//    @Test
//    public void updateOrderStatus() throws Exception{
//
//        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/orders/{id}/confirm",1L,1L)
//                        .header("authorization", adminToken)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
//                .andDo(MockMvcResultHandlers.print());
//    }
//}
