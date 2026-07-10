package dev.tauri.jsg.core.common.util;

import java.util.Calendar;

public class TimeUtils {
    public static boolean isAprilFirst() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1 && Calendar.getInstance().get(Calendar.MONTH) == Calendar.APRIL;
    }

    public static boolean isWinter() {
        var month = Calendar.getInstance().get(Calendar.MONTH);
        return month < Calendar.FEBRUARY || month >= Calendar.DECEMBER;
    }
}
