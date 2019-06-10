package com.xp.note.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/*
 * 时间工具类
 * 获取当前时间：https://blog.csdn.net/feifei454498130/article/details/6540133
 * 时间戳参数：http://www.runoob.com/java/date-timestamp2date.html
 * 时间戳和时间相互转换：(1) https://blog.csdn.net/Silent_Paladin/article/details/54647438
 *                      (2) https://blog.csdn.net/yxb_yingu/article/details/51166553
 *                      (3) https://blog.csdn.net/sinat_32238399/article/details/80512452
 */


public class TimeUtil {


    //将日期时间转换成毫秒
    public static Long transformateFromDateToMilis(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth, hourOfDay, minute, 00);
        Long time = calendar.getTimeInMillis();
        return time;
    }


    //转化毫秒到时间
    public static String transformateFromMilisToDate(Long clockTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm E");
        Date date2 = new Date(clockTime);
        String str = simpleDateFormat.format(date2);
        return str;
    }


    //转化毫秒到特定格式的时间
    public static String transformateFromMilisToStringDate(Long currerntTime, Long clockTime){

        Long mss = clockTime - currerntTime;

        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;

        String day = null;
        String hour = null;
        String min = null;
        String s = null;

        if (days != 0){
            day = days + "天";
        }else {
            day = "";
        }

        if (hours != 0){
            hour = hours + "小时";
        }else {
            hour = "";
        }

        if (minutes != 0){
            min = minutes + "分";
        }else {
            min = "";
        }

        if (days == 0 && hours == 0 && minutes == 0){
            s = seconds + "s";
        }else {
            s = "";
        }

        String remainTime = "任务还剩:" + day + hour  + min + s;

        Log.d("TAG",remainTime);

        if (mss > 0){
            return remainTime;
        }else {
            return "任务已结束";
        }

    }


    //得到当前时间
    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm E");
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
    }

    //得到当前时间(毫秒)
    public static Long getCurrentMilisTime(){
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    //得到当前时间是周几
    public static String getCurrentWendday(){
        SimpleDateFormat format = new SimpleDateFormat("E");
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
    }


}
