package com.jsdttec.sensordata.util;

import android.text.TextUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhangzhongqiang on 2015/8/5.
 */
public class DateUtil {

    public static String styleLine = "-";
    public static String styleQuotes = ":";

    private static SimpleDateFormat format;

    public static final long Second = 1000;
    public static final long Minute = 60 * Second;
    public static final long Hour = 60 * Minute;
    public static final long Day = 24 * Hour;

    public static String dateToString(Long date) {
        format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String dateToString(String date, String style) {
        StringBuilder builder = new StringBuilder();
        Integer[] a = new Integer[]{3, 5};

        if (date == null || date.length() < 6)
            return "";
        else {
            for (int i = 0; i < date.length(); ++i) {
                if (Arrays.asList(a).contains(i)) {
                    builder.append(date.charAt(i));
                    builder.append(style);
                } else
                    builder.append(date.charAt(i));
            }

        }

        return builder.toString();
    }

    public static String timeToString(String time, String style) {

        StringBuilder builder = new StringBuilder();
        Integer[] a = new Integer[]{1, 3};

        if (time.length() < 5)
            return "";
        else {
            for (int i = 0; i < time.length(); ++i) {
                if (Arrays.asList(a).contains(i)) {
                    builder.append(time.charAt(i));
                    builder.append(style);
                } else
                    builder.append(time.charAt(i));
            }

        }

        return builder.toString();

    }

    public static String dateToStringWithAll(Long date) {
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static String dateToStringWithAllDate(Long date) {
        format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String dateToStringWithChinese(Long date) {
        format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(date);
    }

    public static String dateToStringWithChineseYearAndMonth(Long date) {
        format = new SimpleDateFormat("yyyy年MM月");
        return format.format(date);
    }

    public static String dateToStringWithChineseMonthAndDay(Long date) {
        format = new SimpleDateFormat("MM月dd日");
        return format.format(date);
    }

    public static String dateToStringWithChineseOnlyDay(Long date) {
        format = new SimpleDateFormat("dd日");
        String result = format.format(date);
        if (result.startsWith("0")) result = result.substring(0);
        return result;
    }

    public static String dateToStringWithNone(Long date) {
        format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }

    public static String dateToStringWithSec(Long date) {
        format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(date);
    }

    public static String dateToStringWithHour(Long date) {
        format = new SimpleDateFormat("yyyyMMddHH");
        return format.format(date);
    }

    public static String dateToStringWithTime(Long date) {
        format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }

    public static String dateToStringWithHourMinute(Long date) {
        format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

    public static String dateToStringWithMonth(Long date) {
        format = new SimpleDateFormat("MM-dd");
        return format.format(date);
    }

    public static String dataToStringWithDataTime(Long date) {
        format = new SimpleDateFormat("MM/dd HH:mm:ss");
        return format.format(date);
    }

    public static String dataToStringWithDataTime2(Long date) {
        format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return format.format(date);
    }

    public static String dataToStringWithData(Long date) {
        format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(date);
    }

    public static String dataToStringWithData2(Long date) {
        format = new SimpleDateFormat("yy/MM/dd");
        return format.format(date);
    }

    public static Long dateToLong(String date, String formatStr) {
        format = new SimpleDateFormat(formatStr);
        try {
            Date dateFormat = format.parse(date);
            return dateFormat.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String dateToStringWithDayofWeek(Long date) {
        String[] weekOfDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

        return weekOfDays[getWeekOfDate(new Date(date))];
    }

    public static int getWeekOfDate(Date date) {
        Calendar c = Calendar.getInstance();
        if (date != null) {
            c.setTime(date);
        }
        int week = c.get(Calendar.DAY_OF_WEEK) - 1;
        return week;
    }


    public static void main(String[] args) {
        String str = DateUtil.dateToStringWithChinese(System.currentTimeMillis());
        System.out.println(str);
        int week = DateUtil.getWeekOfDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        System.out.println(week);
    }

    public static long getSettingTime(int hour, int minute, int second, int millisecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);

        return calendar.getTimeInMillis();
    }

    public static boolean isToday(Long date) {
        Calendar currentTime = Calendar.getInstance();

        Calendar today = Calendar.getInstance();
        today.set(Calendar.YEAR, currentTime.get(Calendar.YEAR));
        today.set(Calendar.MONTH, currentTime.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, currentTime.get(Calendar.DAY_OF_MONTH));
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        long today_start = today.getTimeInMillis();
        long today_end = today_start + Day;

        if (date >= today_start && date < today_end)
            return true;

        return false;
    }

    public static long getTime(int year, int month, int day, int hour, int minute, int second) {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.YEAR, year);
        time.set(Calendar.MONTH, month);
        time.set(Calendar.DAY_OF_MONTH, day);
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        time.set(Calendar.SECOND, second);

        return time.getTimeInMillis();
    }

    public static boolean isTimeWithinRange(long startTime, long endTime) {
        long currentTime = System.currentTimeMillis();

        if (currentTime < startTime)
            return false;

        if (currentTime > endTime)
            return false;

        return true;
    }

    public static boolean isTimeWithinRange(long date, long element, int number) {
        long currentTime = System.currentTimeMillis();

        if (date > currentTime)
            return false;

        if (date < currentTime - number * element)
            return false;

        return true;
    }

    /* 0-10分钟 刚刚
       11-60    一小时
       一小时前 两小时前 三小时前
       今天以内：显示“今天 几点几分”
       一天之后三十天之内，显示日期
       三十天之后, 显示一个月前*/
    public static String showDateRange(Long date) {
        String dateRange;

        Calendar currentTime = Calendar.getInstance();
        long timeDifference = currentTime.getTimeInMillis() - date;

        Calendar today = Calendar.getInstance();
        today.set(Calendar.YEAR, currentTime.get(Calendar.YEAR));
        today.set(Calendar.MONTH, currentTime.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, currentTime.get(Calendar.DAY_OF_MONTH));
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        if (date > currentTime.getTimeInMillis()) {
            // 服务器的时间和本地也许存在部分偏差，在11分钟以内的都算作刚刚发布的，如果超过太久就采用原来(即else block)的判断方式
            if (Math.abs(timeDifference) < 11 * Minute) {
                dateRange = "刚刚";
            } else {
                dateRange = dateToStringWithChineseMonthAndDay(date);
            }
        } else {
            if (timeDifference >= 0 && timeDifference < 11 * Minute) {
                dateRange = "刚刚";
            } else if (timeDifference >= 11 * Minute && timeDifference < 61 * Minute) {
                dateRange = "1小时";
            } else if (timeDifference >= 61 * Minute && timeDifference < 2 * Hour) {
                dateRange = "1小时前";
            } else if (timeDifference >= 2 * Hour && timeDifference < 3 * Hour) {
                dateRange = "2小时前";
            } else if (timeDifference >= 3 * Hour && timeDifference < 4 * Hour) {
                dateRange = "3小时前";
            } else if (date >= today.getTimeInMillis()) {
                dateRange = "今天 " + dateToStringWithHourMinute(date);
            } else if (date < today.getTimeInMillis() && date >= today.getTimeInMillis() - 30 * Day) {
                dateRange = dateToStringWithChineseMonthAndDay(date);
            } else {
                dateRange = "一个月前";
            }
        }

        return dateRange;
    }

    public static String showSimpleData(String data) {
        if (TextUtils.isEmpty(data))
            return "-";

        if (data.length() > 5)
            return data.substring(0, 5);
        else
            return data;
    }

}
