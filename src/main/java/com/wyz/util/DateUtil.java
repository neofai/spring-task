package com.wyz.util;

import com.wyz.data.emun.DateTimeType;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.time.OffsetDateTime;

@UtilityClass
public class DateUtil {

    private static OffsetDateTime getBeforeOrAfterDateTime(OffsetDateTime time, boolean isBefore, int type, int value) {
        switch (DateTimeType.getTypeByValue(type)) {
            case SECOND:
                return isBefore ? time.minusSeconds(value) : time.plusSeconds(value);
            case MINUTE:
                return isBefore ? time.minusMinutes(value) : time.plusMinutes(value);
            case HOUR:
                return isBefore ? time.minusHours(value) : time.plusHours(value);
            case DAY:
                return isBefore ? time.minusDays(value) : time.plusDays(value);
            case WEEK:
                return isBefore ? time.minusWeeks(value) : time.plusWeeks(value);
            case MONTH:
                return isBefore ? time.minusMonths(value) : time.plusMonths(value);
            case YEAR:
                return isBefore ? time.minusYears(value) : time.plusYears(value);
        }
        throw new RuntimeException();
    }

    public static OffsetDateTime getBeforeDateTime(@NonNull OffsetDateTime time, int type, int value) {
        return getBeforeOrAfterDateTime(time, true, type, value);
    }

    public static OffsetDateTime getAfterDateTime(@NonNull OffsetDateTime time, int type, int value) {
        return getBeforeOrAfterDateTime(time, false, type, value);
    }
}
