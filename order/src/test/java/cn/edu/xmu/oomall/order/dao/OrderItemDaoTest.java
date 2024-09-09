//package cn.edu.xmu.oomall.order.dao;
//
//import cn.edu.xmu.oomall.order.OrderTestApplication;
//import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest(classes = OrderTestApplication.class)
//@Transactional
//public class OrderItemDaoTest {
//    @Autowired
//    private OrderItemDao orderItemDao;
//
//    @Test
//    public void retrieveByOrderIdTest(){
//        List<OrderItem> orderItems= orderItemDao.retrieveByOrderId(1L).getList();
//        assertThat(orderItems.get(0).getId()).isEqualTo(11864L);
//    }
//
//    @Test
//    public void retrieveByActivityIdTest(){
//        List<OrderItem> orderItems= orderItemDao.retrieveByActivityId(1L).getList();
//    }
//
//}
