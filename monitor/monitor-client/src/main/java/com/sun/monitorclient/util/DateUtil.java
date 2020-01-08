package com.sun.monitorclient.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {


    /**
     * 获取当前时间，返回格式yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getNow(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }
}
