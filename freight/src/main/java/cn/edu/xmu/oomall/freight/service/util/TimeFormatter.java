package cn.edu.xmu.oomall.freight.service.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeFormatter {
    public static LocalDateTime StrToLocalDateTime(String time) {
        if(time==null) return null;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        StringBuilder stringBuilder = new StringBuilder(time.split("\\+")[0]);
        stringBuilder.append(".001");
        time = stringBuilder.toString().replace("T", " ");
        LocalDateTime localDateTime = LocalDateTime.parse(time,df);
        return localDateTime;
    }

}
