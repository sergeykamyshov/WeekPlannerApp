package ru.sergeykamyshov.weekplanner.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

    public static Date getWeekStartDate(Date date) {
        Calendar calendar = getStartDateByTime(date);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int firstDayOfWeek = calendar.getFirstDayOfWeek();

        if (dayOfWeek == firstDayOfWeek) {
            return calendar.getTime();
        } else {
            setFirstDayOfWeek(calendar, dayOfWeek, firstDayOfWeek);
            return calendar.getTime();
        }
    }

    public static Date getWeekEndDate(Date date) {
        Calendar calendar = getEndDateByTime(date);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int firstDayOfWeek = calendar.getFirstDayOfWeek();

        if (dayOfWeek == firstDayOfWeek) {
            calendar.add(Calendar.DATE, 6);
            return calendar.getTime();
        } else {
            setFirstDayOfWeek(calendar, dayOfWeek, firstDayOfWeek);
            // Устанавливаем последний день недели
            calendar.add(Calendar.DATE, 6);
            return calendar.getTime();
        }
    }

    public static Date getNextWeekStartDate(Date date) {
        Calendar calendar = getStartDateByTime(date);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int firstDayOfWeek = calendar.getFirstDayOfWeek();

        if (dayOfWeek == firstDayOfWeek) {
            calendar.add(Calendar.DATE, 7);
            return calendar.getTime();
        } else {
            setFirstDayOfWeek(calendar, dayOfWeek, firstDayOfWeek);
            // Уставливаем как день на следующей неделе
            calendar.add(Calendar.DATE, 7);
            return calendar.getTime();
        }
    }

    public static Date getNextWeekEndDate(Date date) {
        Calendar calendar = getEndDateByTime(date);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int firstDayOfWeek = calendar.getFirstDayOfWeek();

        if (dayOfWeek == firstDayOfWeek) {
            calendar.add(Calendar.DATE, 6);
            calendar.add(Calendar.DATE, 7);
            return calendar.getTime();
        } else {
            setFirstDayOfWeek(calendar, dayOfWeek, firstDayOfWeek);
            // Устанавливаем последний день недели
            calendar.add(Calendar.DATE, 6);
            // Устанавливаем как день на следующей неделе
            calendar.add(Calendar.DATE, 7);
            return calendar.getTime();
        }
    }

    public static Date getPreviousWeekStartDate(Date date) {
        Calendar calendar = getStartDateByTime(date);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int firstDayOfWeek = calendar.getFirstDayOfWeek();

        if (dayOfWeek == firstDayOfWeek) {
            calendar.add(Calendar.DATE, -7);
            return calendar.getTime();
        } else {
            setFirstDayOfWeek(calendar, dayOfWeek, firstDayOfWeek);
            // Уставливаем как день на прошлой неделе
            calendar.add(Calendar.DATE, -7);
            return calendar.getTime();
        }
    }

    public static Date getPreviousWeekEndDate(Date date) {
        Calendar calendar = getEndDateByTime(date);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int firstDayOfWeek = calendar.getFirstDayOfWeek();

        if (dayOfWeek == firstDayOfWeek) {
            calendar.add(Calendar.DATE, 6);
            calendar.add(Calendar.DATE, -7);
            return calendar.getTime();
        } else {
            setFirstDayOfWeek(calendar, dayOfWeek, firstDayOfWeek);
            // Устанавливаем последний день недели
            calendar.add(Calendar.DATE, 6);
            // Устанавливаем как день на прошлой неделе
            calendar.add(Calendar.DATE, -7);
            return calendar.getTime();
        }
    }

    private static void setFirstDayOfWeek(Calendar calendar, int dayOfWeek, int firstDayOfWeek) {
        int dif = firstDayOfWeek - dayOfWeek;
        // Для локалей, отличных от "US" подобных, разница будет равна "1".
        // Поэтому мы можем быть уверены что текущий день это конец недели
        if (dif > 0) {
            dif = -6;
        }
        // Устанавливаем первый день недели
        calendar.add(Calendar.DATE, dif);
    }

    private static Calendar getStartDateByTime(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    private static Calendar getEndDateByTime(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

}
