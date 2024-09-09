package cn.edu.xmu.oomall.freight.service.openfeign.ZtoParam;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ZtoPostExpressParam {
    private String body;

    @Data
    @NoArgsConstructor
    public class Body {
        /**
         * 合作模式 ，1：集团客户；2：非集团客户
         */
        private String partnerType = "2";
        /**
         * partnerType为1时，orderType：1：全网件 2：预约件。
         * partnerType为2时，orderType：1：全网件 2：预约件（返回运单号） 3：预约件（不返回运单号） 4：星联全网件
         */
        private String orderType = "1";
        /**
         * 合作商订单号
         */
        private String partnerOrderCode;
        /**
         * 账号信息
         */
        private AccountDto accountInfo;
        /**
         * 发件人信息
         */
        private SenderInfoInput senderInfo;
        /**
         * 收件人信息
         */
        private ReceiveInfoInput receiveInfo;
    }
    @Data
    @NoArgsConstructor
    public class AccountDto{
        /**
         * 电子面单账号（partnerType为2，orderType传1,2,4时必传）
         */
        private String accountId = "test";
        /**
         * 客户编码（partnerType传1时必传）
         */
        private String customerId = "GPG1576724269";
        /**
         * 单号类型:1.普通电子面单；74.星联电子面单；默认是1
         */
        private short type = 1;
    }
    @Data
    @NoArgsConstructor
    public class SenderInfoInput{
        private String senderMobile;
        private String senderName;
        private String senderAddress;
        private String senderDistrict;
        private String senderProvince;
        private String senderCity;
    }
    @Data
    @NoArgsConstructor
    public class ReceiveInfoInput{
        private String receiverMobile;
        private String receiverName;
        private String receiverAddress;
        private String receiverDistrict;
        private String receiverProvince;
        private String receiverCity;
    }

}
