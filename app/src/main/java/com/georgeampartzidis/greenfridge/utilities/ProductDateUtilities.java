package com.georgeampartzidis.greenfridge.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by georgeampartzidis on 02/02/2018.
 */

public final class ProductDateUtilities {

    public final String TAG = ProductDateUtilities.class.getSimpleName();

    public static long todayDateInMillis() {
        Calendar calendar = Calendar.getInstance();
        Date dateToday = calendar.getTime();
        long dateTodayInMillis = calendar.getTimeInMillis();

        return dateTodayInMillis;
    }

    public static long convertDateStringToMillis(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        long dateInMillis = 0;
        try {
            Date date = dateFormat.parse(dateString);
            dateInMillis = date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            return dateInMillis;
        }
    }

    public static String convertMillisToDateString(long dateInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        Date date = new Date(dateInMillis);
        String dateString = dateFormat.format(date);

        return dateString;
    }

}

