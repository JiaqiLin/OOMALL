package cn.edu.xmu.oomall.freight.service.openfeign.SfParam;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class SfPostExpressParam {
    @JsonIgnore
    private String msgBody;
    /**
     * 合作伙伴编码（即顾客编码）
     */
    private String partnerID;
    /**
     * 请求唯一号UUID
     */
    private String requestID;
    /**
     * 接口服务代码 EXP_RECE_CREATE_ORDER
     */
    private String serviceCode;
    /**
     * 调用接口时间戳
     */
    private long timestamp;

    private Order msgData;
    @Data
    public class Order{
        /**
         * 响应报文的语言， 缺省值为zh-CN，目前支持以下值zh-CN 表示中文简体， zh-TW或zh-HK或 zh-MO表示中文繁体， en表示英文
         */
        private String language;
        /**
         * 客户订单号，不能重复
         */
        private String orderId;
        /**
         * 托寄物信息
         */
        private List<CargoDetail> cargoDetails;
        /**
         * 收寄双方信息
         */
        private List<ContactInfo> contactInfoList;
        /**
         * 顺丰月结卡号
         */
        private String monthlyCard;
        /**
         * 快件产品类别， 支持附录 《快件产品类别表》 的产品编码值，仅可使用与顺丰销售约定的快件产品
         */
        private Integer expressTypeId;
        /**
         * 是否返回路由标签： 默认1， 1：返回路由标签， 0：不返回；除部分特殊用户外，其余用户都默认返回
         */
        private Integer isReturnRoutelabel;

    }
    @Data
    public class ContactInfo{
        /**
         *地址类型： 1，寄件方信息 2，到件方信息
         */
        private Integer contactType;
        /**
         * 联系人
         */
        private String 	contact;
        /**
         * 手机
         */
        private String mobile;
        /**
         * 国家或地区2位代码 参照附录《城市代码表》
         */
        private String 	country;
        /**
         * 所在省级行政区名称，必须是标准的省级行政区名称如：北 京、广东省、广西壮族自治区等；
         */
        private String province;
        /**
         * 所在地级行政区名称，必须是标准的城市称谓 如：北京市、 深圳市、大理白族自治州等
         */
        private String city;
        /**
         * 	所在县/区级行政区名称，必须 是标准的县/区称谓，如：福田区，南涧彝族自治县、准格尔旗等
         */
        private String county;
        /**
         * 详细地址，若有四级行政区划，如镇/街道等信息可拼接至此字段，格式样例：镇/街道+详细地址。
         */
        private String address;

    }
    @Data
    public class CargoDetail{
        /**
         * 	货物名称
         */
        private String name;
    }
}

