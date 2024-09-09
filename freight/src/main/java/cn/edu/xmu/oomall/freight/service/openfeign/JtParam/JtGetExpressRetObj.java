package cn.edu.xmu.oomall.freight.service.openfeign.JtParam;

import cn.edu.xmu.oomall.freight.dao.bo.Express;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.ast.Expr;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class JtGetExpressRetObj {
    /**
     * 运输时间上限
     */
    private Long upperLimitTransportTime=1296000L;
    /**
     * 返回码
     */
    private String code;
    /**
     * 描述
     */
    private String msg;
    /**
     * 业务数据
     */
    private List<BusinessData> data;
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    public static class BusinessData{
        /**
         * 运单号
         */
        private String billCode;
        /**
         * 运单轨迹详情
         */
        private List<Detail> details;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    public static class Detail{
        /**
         * 扫描时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private LocalDateTime scanTime;
        /**
         * 轨迹描述
         */
        private String desc;
        /**
         * 扫描类型
         * 1、快件揽收
         * 2、入仓扫描（停用）
         * 3、发件扫描
         * 4、到件扫描
         * 5、出仓扫描
         * 6、入库扫描
         * 7、代理点收入扫描
         * 8、快件取出扫描
         * 9、出库扫描
         * 10、快件签收
         * 11、问题件扫描
         */
        private String scanType;
        /**
         * A1、客户取消寄件-网点
         * A2、客户拒收
         * A3、更改派送地址
         * A4、退回件-网点
         * A5、疫情退回-网点
         * A6、疫情延迟
         * A7、疫情延迟
         * A8、包裹异常-网点
         * A9、收件人联系不上
         * A10、收件人联系不上
         * A11、多次派件失败
         * A12、收件人信息错误
         * A13、更改派送时间
         * A14、客户拒收
         * A15、收件地址不详
         * A16、收件地址错误
         * A17、包裹存放至网点
         * A18、包裹存放至网点
         * A19、收件地址禁止
         * A20、包裹异常
         * A21、包裹暂存网点
         * A22、疫情退回-中心
         * A23、包裹延迟-节假日
         * A24、包裹异常-中心
         * A25、退回件-中心
         * A26、客户取消寄件-中心
         */
        private String problemType;
    }

    public Byte getByteStatus() {
        //一条记录都没有 未揽件
        if(data.get(0).getDetails()==null||data.get(0).getDetails().size()==0){
            return Express.NO_SEND;
        }
        //有记录
        Byte status=null;
        Detail lastDetail=data.get(0).getDetails().get(0);
        //匹配扫描类型
        switch(lastDetail.scanType) {
            case "快件揽收":
            case "发件扫描":
            case "到件扫描":
            case "出仓扫描":
            case "入库扫描":
            case "代理点收入扫描":
            case "快件取出扫描":
            case "出库扫描":
            case "问题件扫描":
                status=Express.SENDING;
                break;
            case "快件签收":
                status=Express.SIGN_FOR;
                break;
        }
        //匹配错误类型
        if(lastDetail.problemType!=null) {
            switch (lastDetail.problemType) {
                case "客户取消寄件-网点":
                    status=Express.CANCEL;
                    break;
                case "客户拒收":
                    status=Express.REJECT_SIGN_FOR;
                    break;
                case "退回件-网点":
                    status=Express.BACK;
                    break;
            }

        }
        if(status==Express.SENDING||status== Express.REJECT_SIGN_FOR){
            //判断是否很久未改变状态
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(lastDetail.scanTime,now);
            Long millis = duration.toMillis();//相差毫秒数
            if(millis>upperLimitTransportTime){
                status=Express.MISS;
            }
        }

        return status;
    }
}
