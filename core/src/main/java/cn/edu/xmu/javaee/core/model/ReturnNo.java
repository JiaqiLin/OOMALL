//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.javaee.core.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回的错误码
 * @author Ming Qiu
 */
public enum ReturnNo {
    /***************************************************
     *    系统级返回码
     **************************************************/

    //状态码 200
    OK(0,"成功"),
    CREATED(1, "创建成功"),
    STATENOTALLOW(7,"%s对象（id=%d）%s状态禁止此操作"),
    RESOURCE_FALSIFY(11, "信息签名不正确"),

    //状态码 404
    RESOURCE_ID_NOTEXIST(4,"%s对象(id=%d)不存在"),

    //状态码 500
    INTERNAL_SERVER_ERR(2,"服务器内部错误"),
    APPLICATION_PARAM_ERR(20, "服务器配置参数(%s)错误"),

    //所有需要登录才能访问的API都可能会返回以下错误
    //状态码 400
    FIELD_NOTVALID(3,"%s字段不合法"),
    IMG_FORMAT_ERROR(8,"图片格式不正确"),
    IMG_SIZE_EXCEED(9,"图片大小超限"),
    PARAMETER_MISSED(10, "缺少必要参数"),
    LATE_BEGINTIME(19, "开始时间不能晚于结束时间"),


    //状态码 401
    AUTH_INVALID_JWT(5,"JWT不合法"),
    AUTH_JWT_EXPIRED(6,"JWT过期"),
    AUTH_INVALID_ACCOUNT(12, "用户名不存在或者密码错误"),
    AUTH_ID_NOTEXIST(13,"登录用户id不存在"),
    AUTH_USER_FORBIDDEN(14,"用户被禁止登录"),
    AUTH_NEED_LOGIN(15, "需要先登录"),

    //状态码 403
    AUTH_NO_RIGHT(16, "无权限"),
    RESOURCE_ID_OUTSCOPE(17,"%s对象(id=%d)超出商铺（id = %d）的操作范围"),
    FILE_NO_WRITE_PERMISSION(18,"目录文件夹没有写入的权限"),

    /**************************************
     *  支付模块
     ************************************/
    PAY_CHANNEL_INVALID(102,"%s支付渠道无效"),
    PAY_CHANNEL_EXIST(103,"商铺(id=%d)的支付渠道(id=%d)已经存在"),
    PAY_REFUND_MORE(104,"退款金额超过支付对象(id=%d)的金额"),
    PAY_DIVREFUND_MORE(105,"分账退回金额超过支付对象(id=%d)的支付分账金额"),

    /***************************************************
     *    商铺模块错误码
     **************************************************/
    SHOP_USER_HASSHOP(199,"用户(id= %d)已经有店铺"),

    FREIGHT_REGION_NOTREACH(198,"该地区不可达（包括暂停和不在配送范围）"),
    FREIGHT_REGIONOBSOLETE(197,"地区已废弃"),
    FREIGHT_REGIONEXIST(195,"该运费模板中该地区(id=%d)已经定义"),

    /***************************************************
     *    团购模块错误码
     **************************************************/

    /***************************************************
     *    优惠模块错误码
     **************************************************/
    COUPON_NOTBEGIN(501,"未到优惠卷领取时间"),
    COUPON_FINISH(502,"优惠卷领罄"),
    COUPON_END(503,"优惠卷活动终止"),
    COUPON_EXIST(504,"不可重复领优惠卷"),
    /***************************************************
     *    分享模块错误码
     **************************************************/
    SHARE_UNSHARABLE(599, "货品不可分享"),
    /***************************************************
     *    顾客模块错误码
     **************************************************/
    ADDRESS_OUTLIMIT(601,"达到地址簿上限"),

    ORDERITEM_NOTSHARED(606,"订单明细无分享记录"),
    CUSTOMERID_NOTEXIST(608,"登录用户id不存在"),
    CUSTOMER_INVALID_ACCOUNT(609, "用户名不存在或者密码错误"),
    CUSTOMER_FORBIDDEN(610,"用户被禁止登录"),
    CUSTOMER_MOBILEEXIST(611,"电话已被注册"),
    CUSTOMER_EMAILEXIST(612,"邮箱已被注册"),
    CUSTOMER_NAMEEXIST(613,"用户名已被注册"),
    CUSTOMER_PASSWORDSAME(614,"不能与旧密码相同"),

    /**************************************
     *  售后模块
     ************************************/
    ARBITRATION_NOTSELF(701, "仲裁(id=%d)非本用户受理的仲裁"),
    ARBITRATION_NOT_APPLICANT(702, "仲裁(id=%d)仅申请人可以撤销"),
    AFTERSALE_NOT_RETURNCHANGE(703, "(id=%d)不是退换货售后"),
    AFTERSALE_NOT_SELFSENDBACK(704, "(id=%d)不是自行寄回售后"),

    /**************************************
     *  服务模块
     ************************************/


    /***************************************************
     *    订单模块错误码
     **************************************************/
    ORDER_CHANGENOTALLOW(801,"订单(id=%d)地址费用变化"),
    ITEM_OVERMAXQUANTITY(802,"销售对象(id=%d)的数量(%d)超过单次可购买数量(%d)"),
    ITEMS_NOTENOUGH(296,"货品（id = %d）库存不足"),

    /***************************************************
     *    商品模块错误码
     **************************************************/
    GOODS_CATEGORY_SAME(901, "类目(id = %d)名称已存在"),
    GOODS_PRICE_CONFLICT(902,"商品(id = %d)销售时间冲突"),
    GOODS_CATEGORY_NOTALLOW(903, "不允许加入到一级分类"),
    GOODS_STOCK_SHORTAGE(904,"货品（id = %d）库存不足"),
    GOODS_ONSALE_NOTEFFECTIVE(905, "货品（id=%d）不在有效的销售状态和时间"),
    SHOP_CATEGORY_NOTPERMIT(906, "不允许增加新的下级分类"),

    /**************************************
     *  物流模块
     ************************************/
    FREIGHT_BESTMATCH_FAIL(994,"匹配最佳仓库物流失败"),
    FREIGHT_SHOPLOGISTICS_INVALID(995,"(id=%d)商铺物流无效"),
    FREIGHT_BILLCODE_NOTEXIST(996,"运单号不存在"),
    FREIGHT_WAREHOUSEREGION_EXIST(997, "重复设置地区"),
    FREIGHT_WAREHOUSELOGISTIC_EXIST(998, "重复设置物流"),
    FREIGHT_LOGISTIC_EXIST(999, "商铺已存在物流(id=%d)");



    private int errNo;
    private String message;
    private static final Map<Integer, ReturnNo> returnNoMap = new HashMap() {
        {
            for (Object enum1 : values()) {
                put(((ReturnNo) enum1).errNo, enum1);
            }
        }
    };

    ReturnNo(int code, String message){
        this.errNo = code;
        this.message = message;
    }

    public static ReturnNo getByCode(int code1) {
        ReturnNo[] all=ReturnNo.values();
        for (ReturnNo returnNo :all) {
            if (returnNo.errNo==code1) {
                return returnNo;
            }
        }
        return null;
    }
    public static ReturnNo getReturnNoByCode(int code){
        return returnNoMap.get(code);
    }
    public int getErrNo() {
        return errNo;
    }

    public String getMessage(){
        return message;
    }


    }
