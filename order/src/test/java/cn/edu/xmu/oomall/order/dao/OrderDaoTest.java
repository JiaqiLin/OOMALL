//package cn.edu.xmu.oomall.order.dao;
//
//import cn.edu.xmu.javaee.core.exception.BusinessException;
//import cn.edu.xmu.oomall.order.OrderApplication;
//import cn.edu.xmu.oomall.order.OrderTestApplication;
//import cn.edu.xmu.oomall.order.dao.bo.Order;
//import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@SpringBootTest(classes = OrderTestApplication.class)
//@Transactional
//public class OrderDaoTest {
//    @Autowired
//    private OrderDao orderDao;
//
//    @Test
//    public void findByIdTest1(){
//        Order order = new Order();
//        order.setId(Long.valueOf(1));
//        order.setCustomerId(Long.valueOf(1));
//        order.setShopId(Long.valueOf(1));
//        order.setOrderSn("2016102361242");
//        order.setAddress("人民北路");
//
//        Order ret = orderDao.findById(Long.valueOf(1));
//
//        assertThat(ret.getId()).isEqualTo(order.getId());
//        assertThat(ret.getCustomerId()).isEqualTo(order.getCustomerId());
//        assertThat(ret.getShopId()).isEqualTo(order.getShopId());
//        assertThat(ret.getOrderSn()).isEqualTo(order.getOrderSn());
//        assertThat(ret.getAddress()).isEqualTo(order.getAddress());
//    }
//
//    @Test
//    public void findByShopIdAndOrderIdTest1(){
//        Order order=orderDao.findByShopIdAndOrderId(1L,1L);
//        assertThat(order.getCustomerId()).isEqualTo(1L);
//        assertThat(order.getOrderSn()).isEqualTo("2016102361242");
//        assertThat(order.getConsignee()).isEqualTo("赵永波");
//    }
//
//    @Test
//    public void findByShopIdAndOrderIdTest2(){
//        assertThrows(BusinessException.class,()->orderDao.findByShopIdAndOrderId(1L,3L));
//    }
//
//    @Test
//    public void retrieveByShopIdTest1(){
//        List<Order> ret=orderDao.retrieveByShopId(1L,null,null,null,null,1,10);
//        System.out.println(ret.size());
//        assertThat(ret.get(1).getOrderSn()).isEqualTo("2016102378405");
//        assertThat(ret.get(2).getId()).isEqualTo(4L);
//    }
//
//    @Test
//    public void retrieveByShopIdTest2(){
//        List<Order> ret=orderDao.retrieveByShopId(1L,2L,null,null,null,1,10);
//        assertThat(ret.get(0).getOrderSn()).isEqualTo("2016102378405");
//        assertThat(ret.get(0).getConsignee()).isEqualTo("赵俊峻");
//    }
//
//    @Test
//    public void retrieveByShopIdTest3(){
//        List<Order> ret=orderDao.retrieveByShopId(1L,null,"2016102364965",null,null,1,10);
//        assertThat(ret.get(0).getId()).isEqualTo(4L);
//        assertThat(ret.get(0).getRegionId()).isEqualTo(2417L);
//    }
//
//    @Test
//    public void retrieveByShopIdTest4(){
//        List<Order> ret=orderDao.retrieveByShopId(1L,null,null, LocalDateTime.parse("2016-10-23T12:26:43"),null,1,10);
//        assertThat(ret.get(0).getId()).isEqualTo(10L);
//    }
//
//    @Test
//    public void retrieveByShopIdTest5(){
//        List<Order> ret=orderDao.retrieveByShopId(1L,2L,"2016102378405",null,null,1,10);
//        assertThat(ret.get(0).getConsignee()).isEqualTo("赵俊峻");
//    }
//
//    @Test
//    public void retrieveByShopIdTest6(){
//        assertThrows(BusinessException.class,()->orderDao.retrieveByShopId(1L,333333333L,null,null,null,1,10));
//    }
//
//    @Test
//    public void createOrder(){
//        List<OrderItem> orderItemList = new ArrayList<>();
//        OrderItem orderItem = new OrderItem(1L,2L,"amz",null,null,null,null,1L,1L,1,5000L,0L,0L,"牛奶",1L,1L,null);
//        orderItem.setProductId(1L);
//        orderItem.setFreightTemplateId(1L);
//        orderItem.setWeight(250L);
//        orderItemList.add(orderItem);
//        Order order = new Order(1L,1L,"amz",null,null,null,null,1L,1L,"123",null,"amz",1L,"郊区","13017238291","请尽快发货",1L,1L,orderItemList);
//        orderDao.createOrder(order);
//
//    }
//
//    @Test
//    public void delByIdTest(){
//        Order order= orderDao.findById(1L);
//        orderDao.delById(order);
//        assertThrows(BusinessException.class,()->orderDao.findById(1L)) ;
//    }
//}
