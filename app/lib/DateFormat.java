package lib;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.time.Month;
import java.util.HashMap;
import java.util.TimeZone;

public class DateFormat {

    private static HashMap<Integer, String> monthMap = new HashMap<Integer, String>();

    static {
        monthMap.put(1, "Jan");
        monthMap.put(2, "Feb");
        monthMap.put(3, "Mar");
        monthMap.put(4, "Apr");
        monthMap.put(5, "May");
        monthMap.put(6, "Jun");
        monthMap.put(7, "Jul");
        monthMap.put(8, "Aug");
        monthMap.put(9, "Sep");
        monthMap.put(10, "Oct");
        monthMap.put(11, "Nov");
        monthMap.put(12, "Dec");
    }

    public static String timeToString(long time) {
        DateTime date = new DateTime(time, DateTimeZone.forTimeZone(TimeZone.getDefault()));
        DateTime currDate = new DateTime(System.currentTimeMillis(), DateTimeZone.forTimeZone(TimeZone.getDefault()));

        String prefix = DateTimeFormat.forPattern("HH:mm:ss").print(date);

        if (currDate.getYear() == date.getYear()) {
            if (currDate.getMonthOfYear() == date.getMonthOfYear()) {
                if (currDate.getDayOfMonth() == date.getDayOfMonth()) {
                    return prefix;
                }
            }
            return prefix + " " + date.getDayOfMonth() + " " + monthMap.get(date.getMonthOfYear());
        } else {
            return prefix + " " + date.getDayOfMonth() + " " + monthMap.get(date.getMonthOfYear()) + " " + date.getYear();
        }
    }
}
