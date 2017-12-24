package com.monke.monkeybook.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者:  lbqiang on 2017/12/24 22:49
 * 邮箱:  anworkmail_q@126.com
 * 作用:
 */

public class TimeUtils {
    public static String getTimeStringByLongMills(long timeLong) {
        if (timeLong > 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒");
            Date date = new Date(timeLong);
            return formatter.format(date);
        } else {
            return "";
        }
    }
}
