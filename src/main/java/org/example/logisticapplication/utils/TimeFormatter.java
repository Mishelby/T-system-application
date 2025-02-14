package org.example.logisticapplication.utils;

public class TimeFormatter {

    public static String formatHours(Double time) {
        String suffix = determineSuffix(time);
        return time + " " + suffix;
    }

    private static String determineSuffix(Double time) {
        if (time % 10 == 1 && time % 100 != 11) {
            return "час";
        } else if ((time % 10 >= 2 && time % 10 <= 4) && (time % 100 < 10 || time % 100 >= 20)) {
            return "часа";
        } else {
            return "часов";
        }
    }
}

