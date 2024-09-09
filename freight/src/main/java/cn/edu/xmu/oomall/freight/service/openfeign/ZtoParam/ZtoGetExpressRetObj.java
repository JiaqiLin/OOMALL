package cn.edu.xmu.oomall.freight.service.openfeign.ZtoParam;

import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.service.util.TimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class ZtoGetExpressRetObj {
    /**
     * 运输时间上限
     */
    private Long upperLimitTransportTime=1296000L;
    /**
     * 返回信息
     */
    private String message;
    /**
     * 返回code
     */
    private String statusCode;
    /**
     * 返回状态
     */
    private Boolean status;
    /**
     * 返回结果
     */
    private List<BillTrackOutput> result;
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    public static class BillTrackOutput{
        /**
         * 扫描时间
         */
        @JsonDeserialize(using=TimeDeserializer.class)
        private LocalDateTime scanDate;
        /**
         * 轨迹描述
         */
        private String desc;
        /**
         *
         * 扫描类型:收件 、发件、 到件、 派件、 签收、退件、问题件、ARRIVAL （派件入三方自提柜等)、SIGNED（派件出三方自提柜等）
         */
         private String scanType;

    }

    public Byte getByteStatus(){
        if(result.size() == 0){
            return Express.NO_SEND;
        }

        Byte status = null;
        BillTrackOutput lastResult = result.get(0);
        switch (lastResult.scanType){
            case "收件":
            case "发件":
            case "到件":
            case "派件":
            case "问题件":
            case "Arrival":
                status = Express.SENDING;
                break;
            case "签收":
            case "SIGNED":
                status = Express.SIGN_FOR;
                break;
            case "退件":
                status = Express.BACK;
                break;
        }
        if(status==Express.SENDING||status== Express.REJECT_SIGN_FOR){
            //判断是否很久未改变状态
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(lastResult.scanDate,now);
            Long millis = duration.toMillis();//相差毫秒数
            if(millis>upperLimitTransportTime){
                status=Express.MISS;
            }
        }
        return status;
    }
}
