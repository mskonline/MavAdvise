package org.mavadvise.commons;

import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Calendar;

public class Utils {

    public static String hashString(String str){
        String hashedStr = str;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(str.getBytes());

            hashedStr = String.format("%0" + (d.length*2) + "X", new BigInteger(1, d));
        } catch (Exception e) {
            Log.e("Utils", "Hashing error " + e.getMessage());
        }

        return hashedStr;
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
}
