package com.wyz.data.emun;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DateTimeType {
    NONE(0),
    SECOND(1),
    MINUTE(2),
    HOUR(3),
    DAY(4),
    WEEK(5),
    MONTH(6),
    YEAR(7);

    private final Integer value;

    public static DateTimeType getTypeByValue(Integer value) {
        if (value == null) return null;
        for (DateTimeType dateTimeType : DateTimeType.values()) {
            if (dateTimeType.getValue() == value) {
                return dateTimeType;
            }
        }
        return null;
    }
}
