package com.kttt.webbanve;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    static Calendar calendar = Calendar.getInstance();
    public static Date stringToDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(date);
    }
    public static int stringToMinute(String hour){
        String splits[] = hour.split(":");
        int hr = Integer.parseInt(splits[0].trim());
        int min = Integer.parseInt(splits[1].trim());
        return hr*60 + min;
    }

    //Compare now with dateFlight add second
    public static int compareTime(Date dateFlight,int min){
        Date now = calendar.getTime();
        calendar.setTime(dateFlight);
        calendar.add(Calendar.MINUTE,min);
        Date landing = calendar.getTime();
        return now.compareTo(landing);
    }
}
