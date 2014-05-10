package com.salvadordalvik.fastlibrary.util;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormat;

/**
 * Created by matthewshepard on 5/9/14.
 */
public class FastDateUtils {
    //YYYY-MM-DD HH:MM:SS
    private static final DateTimeFormatter sqliteTimestamp = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public static DateTime parseSqliteTimestamp(String datetime){
        return sqliteTimestamp.parseDateTime(datetime);
    }

    public static String printSqliteTimestamp(DateTime timestamp){
        return sqliteTimestamp.print(timestamp);
    }

    public static String shortRecentDate(int epoc){
        return shortRecentDate(new DateTime(epoc));
    }

    public static String shortRecentDate(String sqliteTimestamp){
        return shortRecentDate(parseSqliteTimestamp(sqliteTimestamp));
    }

    public static String shortRecentDate(DateTime timestamp){
        Period diff = new Period(timestamp, DateTime.now(), PeriodType.standard());
        PeriodType type;
        if(diff.getMonths() > 0){
            type = PeriodType.yearMonthDay();
        }else if(diff.getWeeks() > 0){
            type = PeriodType.yearWeekDay();
        }else if(diff.getDays() > 0){
            type = PeriodType.dayTime().withSecondsRemoved().withMillisRemoved().withMinutesRemoved();
        }else if(diff.getMinutes() > 0){
            type = PeriodType.time().withMillisRemoved().withSecondsRemoved();
        }else{
            type = PeriodType.time().withMillisRemoved();
        }
        return PeriodFormat.getDefault().print(new Period(timestamp, DateTime.now(), type));
    }
}
