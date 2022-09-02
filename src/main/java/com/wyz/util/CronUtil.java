package com.wyz.util;


import com.wyz.data.TaskDemo;
import com.wyz.data.emun.DateTimeType;
import lombok.experimental.UtilityClass;
import org.springframework.expression.ParseException;
import org.springframework.scheduling.support.CronExpression;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class CronUtil {
    private static final Long SECONDS_OF_DAY = 86400L;
    private static final Long SECONDS_OF_WEEK = SECONDS_OF_DAY * 7;


    public static String getCron(TaskDemo taskDemo) {
        String cron = "";
        cron = getCron(taskDemo.getSetTime(), taskDemo.getRepeatType());
        if (taskDemo.getInAdvanceType() != null && taskDemo.getInAdvanceType() > 0) {
            OffsetDateTime inAdvanceTime = DateUtil.getBeforeDateTime(taskDemo.getSetTime(), taskDemo.getInAdvanceType(), taskDemo.getInAdvanceValue());
            cron = cron + "," + getCron(inAdvanceTime, taskDemo.getRepeatType());
        }
        return cron;
    }

    private static String getCron(OffsetDateTime setTime, Integer repeatType) {
        int minute = setTime.getMinute();
        int hour = setTime.getHour();
        int dayOfMonth = setTime.getDayOfMonth();
        int month = setTime.getMonthValue();
        int dayOfWeek = setTime.getDayOfWeek().getValue();
        switch (DateTimeType.getTypeByValue(repeatType)) {
            case NONE:
                return String.format("0 %s %s %s %s %s", minute, hour, dayOfMonth, month, "?");
            case DAY:
                return String.format("0 %s %s %s %s %s", minute, hour, "*", "*", "?");
            case WEEK:
                return String.format("0 %s %s %s %s %s", minute, hour, "?", "*", dayOfWeek);
            case MONTH:
                return String.format("0 %s %s %s %s %s", minute, hour, dayOfMonth, "*", "?");
            case YEAR:
                return String.format("0 %s %s %s %s %s", minute, hour, dayOfMonth, month, "?");
        }
        return null;
    }


    /**
     * 判断指定时间 至 次日0点0分0秒 是否在cron表达式中, 且间隔要等于设定的间隔
     */
    public static Boolean isInCron(String cron, OffsetDateTime beginTime, OffsetDateTime setTime, int intervalType, Integer value) {
        try {
            // 获取指定时间的次日0点0分0秒
            OffsetDateTime endTime = beginTime.plusDays(1).truncatedTo(ChronoUnit.DAYS);
            CronExpression cronExpression = CronExpression.parse(cron);

            // 获取cron中beginTime之后的下一次执行时间
            OffsetDateTime nextTime = cronExpression.next(beginTime);

            // 对于重复间隔不等于1 的提醒，做额外的判断, 即判断间隔是否等于设定值(目前客户端还不支持设置间隔>1)
            if (intervalType > 0 && nextTime != null && value > 1) {
                if (!intervalEquals(setTime, intervalType, value, nextTime)) {
                    return false;
                }
            }

            // 判断nextTime是否在 endTime 之前/相等
            return nextTime != null && (endTime.isAfter(nextTime) || endTime.isEqual(nextTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断cron是否过期，即nextTime是否大于endTime
     */
    public static Boolean isValidCron(String cron, OffsetDateTime nowTime, OffsetDateTime endTime) {
        // 如果endTime == null, 则认为cron永不过期
        if (endTime == null) {
            return true;
        }

        try {
            CronExpression cronExpression = CronExpression.parse(cron);
            // 获取cron中beginTime之后的下一次执行时间
            OffsetDateTime nextTime = cronExpression.next(nowTime);
            return nextTime != null && (nextTime.isBefore(endTime) || nextTime.isEqual(endTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断 设定重复提醒的间隔 是否等于 参数时间到设定时间的间隔
     * eg: 设定时间为 6月27日 , 间隔为 day, value 为2(即从27日开始，每两天提醒一次) , 参数时间为 28日, 则返回false; 参数时间为29日, 则返回 true
     *
     * @param intervalType 4:"day", 5:"week", 6:"month", 7:"year"
     */
    public static Boolean intervalEquals(OffsetDateTime setTime, int intervalType, Integer value, OffsetDateTime nowTime) {
        long seconds = nowTime.toEpochSecond() - setTime.toEpochSecond();
        switch (DateTimeType.getTypeByValue(intervalType)) {
            case DAY:
                if (seconds % (SECONDS_OF_DAY * value) == 0) {
                    return true;
                }
                break;
            case WEEK:
                if (seconds % (SECONDS_OF_WEEK * value) == 0) {
                    return true;
                }
                break;
            case MONTH:
                int months = (nowTime.getYear() - setTime.getYear()) * 12 + (nowTime.getMonthValue() - setTime.getMonthValue());
                if (months % value == 0) {
                    return true;
                }
                break;
            case YEAR:
                int years = nowTime.getYear() - setTime.getYear();
                if (years % value == 0) {
                    return true;
                }
                break;
            default:
                return false;
        }
        return false;
    }

}
