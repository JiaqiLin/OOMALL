//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.order.dao.OrderItemDao;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@ToString(callSuper = true)
@Data
@NoArgsConstructor
public class Order extends OOMallObject {

    @Builder
    public Order(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, Long customerId, Long shopId, String orderSn, Long pid, String consignee, Long regionId, String address, String mobile, String message, Long activityId, Long packageId, List<OrderItem> orderItems) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.customerId = customerId;
        this.shopId = shopId;
        this.orderSn = orderSn;
        this.pid = pid;
        this.consignee = consignee;
        this.regionId = regionId;
        this.address = address;
        this.mobile = mobile;
        this.message = message;
        this.activityId = activityId;
        this.packageId = packageId;
        this.orderItems = orderItems;
    }


    //10-待付款
    public static final int NEW = 101;//新订单
    public static final int BALANCE = 102;//待支付尾款的订单

    //20-待收货
    public static final int CLUSTER = 202;//待成团
    public static final int PAID = 201;//已付款
    public static final int SEND = 204;//已发货
    public static final int NOTSEND = 203;//待发货


    public static final int COMPLETE = 300;//已完成

    //已取消
    public static final int REFUND = 401;//待退款
    public static final int CANCEL = 402;//已取消

    public static final Map<Integer, String> STATUSNAMES = new HashMap(){
        {
            put(NEW, "新订单");
            put(BALANCE, "待支付尾款的订单");
            put(CLUSTER, "待成团");
            put(PAID, "已付款");
            put(SEND, "已发货");
            put(NOTSEND, "待发货");
            put(COMPLETE, "已完成");
            put(REFUND, "待退款");
            put(CANCEL, "已取消");
        }
    };

    public String getStatusName(){
        return STATUSNAMES.get(this.status);
    }

    private static final Map<Integer, Set<Integer>> toStatus = new HashMap<>(){
        {
            put(NEW, new HashSet<>(){
                {
                    add(CLUSTER);
                    add(PAID);
                    add(BALANCE);
                }
            });
            put(BALANCE, new HashSet<>(){
                {
                    add(PAID);
                    add(CANCEL);
                    add(REFUND);
                }
            });
            put(CLUSTER, new HashSet<>(){
                {
                    add(PAID);
                    add(REFUND);
                }
            });
            put(PAID, new HashSet<>(){
                {
                    add(NOTSEND);
                    add(REFUND);
                    add(COMPLETE);
                }
            });
            put(SEND, new HashSet<>(){
                {
                    add(COMPLETE);
                    add(REFUND);
                }
            });
            put(NOTSEND, new HashSet<>(){
                {
                    add(SEND);
                    add(REFUND);
                }
            });
            put(COMPLETE, new HashSet<>(){
                {
                }
            });
            put(REFUND, new HashSet<>(){
                {
                    add(CANCEL);
                }
            });
            put(CANCEL, new HashSet<>(){
                {
                }
            });
        }
    };

    /**
     * 是否允许状态迁移
     * @author Ming Qiu
     * <p>
     * date: 2022-11-13 0:25
     * @param status
     * @return
     */
    public boolean allowStatus(Integer status){
        boolean ret = false;

        if (null != status && null != this.status){
            Set<Integer> allowStatusSet = toStatus.get(this.status);
            if (null != allowStatusSet) {
                ret = allowStatusSet.contains(status);
            }
        }
        return ret;
    }

    private Integer status;//订单状态
    private Long customerId;//顾客id
    private Long shopId;//商铺id
    private String orderSn;//订单号
    private Long pid;//父订单
    private String consignee;//联系人
    private Long regionId;//地区id
    private String address;//详细地址
    private String mobile;//联系电话
    private String message;//附言
    private Long activityId;//活动id
    private Long packageId;//包裹id
    private List<OrderItem> orderItems;//订单包含的物品细则
    private Long expressFee;//快递费用
    private Long discountPrice;//折扣金额
    private Long originPrice;//折扣前金额
    private Long point;//积点支付

    private OrderItemDao orderItemDao;

    public List<OrderItem> getOrderItems() {
        if (null == this.orderItems && null != this.orderItemDao) {
            this.orderItems = this.orderItemDao.retrieveByOrderId(this.id).getList();
        }
        return orderItems;
    }

}
