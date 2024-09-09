package cn.edu.xmu.oomall.freight.service.openfeign.SfParam;

import cn.edu.xmu.oomall.freight.dao.bo.Express;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class SfGetExpressRetObj {
    /**
     * 运输时间上限
     */
    private Long upperLimitTransportTime=1296000L;
    /**
     * true 请求成功，false 请求失败
     */
    private String success;
    /**
     * 错误编码，S0000成功
     */
    private String 	errorCode;
    /**
     * 错误描述
     */
    private String 	errorMsg;
    /**
     * 返回的详细数据
     */
    private MsgData msgData;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    public static class MsgData{
        private List<RouteResp> routeResps;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    public static class RouteResp{
        /**
         * 	顺丰运单号
         */
        private String mailNo;
        /**
         * 路由信息（列表）
         */
        private List<Route> routes;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    public static class Route{
        /**
         * 路由节点发生的时间，
         * 格式：YYYY-MM-DD HH24:MM:SS，示例：2012-7-30 09:30:00
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private LocalDateTime acceptTime;
        /**
         * 路由节点发生的地点
         */
        private String acceptAddress;
        /**
         * 路由节点具体描述
         */
        private String remark;
        /**
         * 	路由节点操作码
         * 	http://qiao.sf-express.com/developSupport/734349?activeIndex=589678
         */
        private String opcode;

    }

    public Byte getByteStatus(){
        if(msgData.getRouteResps() == null || msgData.getRouteResps().size() == 0
               || msgData.getRouteResps().get(0).getRoutes() == null
                || msgData.getRouteResps().get(0).getRoutes().size() == 0 ){
            // 没有快递信息
            return Express.NO_SEND;
        }

        int lastStatusIndex = msgData.getRouteResps().get(0).getRoutes().size();

        Byte status = null;
        Route route = msgData.getRouteResps().get(0).getRoutes().get(lastStatusIndex - 1);
        switch (route.opcode){
            case "80":
                status = Express.SIGN_FOR;
                break;
            case "3036":
                status = Express.SENDING;
                break;
            case "631":
                status = Express.CANCEL;
        }
        if(status==Express.SENDING||status== Express.REJECT_SIGN_FOR){
            //判断是否很久未改变状态
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(route.acceptTime,now);
            Long millis = duration.toMillis();//相差毫秒数
            if(millis>upperLimitTransportTime){
                status=Express.MISS;
            }
        }
        return status;
    }
}
