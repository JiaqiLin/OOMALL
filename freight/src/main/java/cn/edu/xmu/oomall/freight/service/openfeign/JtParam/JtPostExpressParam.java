package cn.edu.xmu.oomall.freight.service.openfeign.JtParam;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class JtPostExpressParam {
    private String bizContent;
    @Data
    @NoArgsConstructor
    public  class BizContent {
        /**
         * 客户编码（联系出货网点提供）
         */
        private String customerCode;
        /**
         * 签名，Base64(Md5(客户编号+密文+privateKey))，其中密文：MD5(明文密码+jadada236t2) 后大写
         */
        private String digest;
        /**
         * 客户订单号（传客户自己系统的订单号）
         */
        private String txlogisticId;
        /**
         * 快件类型：EZ(标准快递)
         */
        private String expressType="EZ";
        /**
         * 订单类型（有客户编号为月结）1、 散客；2、月结；
         */
        private String orderType="1";
        /**
         * 服务类型 ：02 门店寄件 ； 01 上门取件
         */
        private String serviceType="01";
        /**
         * 派送类型： 06 代收点自提 05 快递柜自提 04 站点自提 03 派送上门
         */
        private String deliveryType="03";
        /**
         * 支付方式：PP_PM("寄付月结"), CC_CASH("到付现结");
         */
        private String payType="PP_PM";
        /**
         * 物品类型（对应订单主表物品类型）:
         * bm000001 文件
         * bm000002 数码产品
         * bm000003 生活用品
         * bm000004 食品
         * bm000005 服饰
         * bm000006 其他
         * bm000007 生鲜类
         * bm000008 易碎品
         * bm000009 液体
         */
        private String goodsType="bm000006";
        /**
         * 重量，单位kg，范围0.01-30 默认0.02
         */
        private String weight="0.02";
        /**
         * 寄件信息对象
         */
        private Consignee sender;
        /**
         * 收件信息对象
         */
        private Consignee receiver;
    }
    @NoArgsConstructor
    @Data
    public class Consignee{
        /**
         * 寄件人姓名
         */
        private String name;
        /**
         * 寄件手机（手机和电话二选一必填）
         */
        private String mobile;
        /**
         * 寄件国家三字码（如：中国=CHN、印尼=IDN）
         */
        private String countryCode="CHN";
        /**
         * 寄件省份
         */
        private String prov;
        /**
         * 寄件城市
         */
        private String city;
        /**
         * 寄件区域
         */
        private String area;
        /**
         * 寄件详细地址（省+市+区县+详细地址）
         */
        private String address;

    }

}
