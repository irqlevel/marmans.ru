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
        monthMap.put(0, "Jan");
        monthMap.put(1, "Feb");
        monthMap.put(2, "Mar");
        monthMap.put(3, "Apr");
        monthMap.put(4, "May");
        monthMap.put(5, "Jun");
        monthMap.put(6, "Jul");
        monthMap.put(7, "Aug");
        monthMap.put(8, "Sep");
        monthMap.put(9, "Oct");
        monthMap.put(10, "Nov");
        monthMap.put(11, "Dec");
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
            return prefix + " " + date.getDayOfMonth() + " " + monthMap.get(date.getDayOfMonth());
        } else {
            return prefix + " " + date.getDayOfMonth() + " " + monthMap.get(date.getDayOfMonth()) + " " + date.getYear();
        }
    }
}
