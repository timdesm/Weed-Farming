package com.knovosky.weedfarming.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {

    public static int stringToSeconds(String timestamp) {
        if(timestamp == null) return 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'GMT'HH:mm:ss");
            Date date = sdf.parse(timestamp);
            long epoch = date.getTime();
            return (int) (epoch/1000);
        } catch(ParseException e) {
            return 0;
        }
    }

    public static Date stringToDate(String timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'GMT'HH:mm:ss");
            Date date = sdf.parse(timestamp);
            return date;
        }
        catch(ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer getTimeLeft(int sec) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'GMT'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();

        Integer tempsRestant = (sec - TimeUtil.stringToSeconds(dateFormat.format(date)));
        return tempsRestant;
    }

}
