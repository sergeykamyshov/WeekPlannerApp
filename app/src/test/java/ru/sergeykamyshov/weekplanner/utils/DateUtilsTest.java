package ru.sergeykamyshov.weekplanner.utils;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest {

    @Test
    public void getWeekStartDate_whenMondayTest() throws Exception {
        Date mondayDate = new GregorianCalendar(2017, Calendar.NOVEMBER, 12, 10, 30).getTime();
        Date expectedDateTime = new GregorianCalendar(2017, Calendar.NOVEMBER, 12).getTime();

        Date weekStartDate = DateUtils.getWeekStartDate(mondayDate);

        assertEquals(expectedDateTime, weekStartDate);
    }

    @Test
    public void getWeekStartDate_whenWednesdayTest() throws Exception {
        Date wednesdayDate = new GregorianCalendar(2017, Calendar.NOVEMBER, 15, 10, 30).getTime();
        Date expectedDateTime = new GregorianCalendar(2017, Calendar.NOVEMBER, 12).getTime();

        Date weekStartDate = DateUtils.getWeekStartDate(wednesdayDate);

        assertEquals(expectedDateTime, weekStartDate);
    }

    @Test
    public void getWeekStartDate_whenSaturdayTest() throws Exception {
        Date saturdayDate = new GregorianCalendar(2017, Calendar.NOVEMBER, 18, 10, 30).getTime();
        Date expectedDateTime = new GregorianCalendar(2017, Calendar.NOVEMBER, 12).getTime();

        Date weekStartDate = DateUtils.getWeekStartDate(saturdayDate);

        assertEquals(expectedDateTime, weekStartDate);
    }

    @Test
    public void getWeekEndDate_whenMondayTest() throws Exception {
        Date mondayDate = new GregorianCalendar(2017, Calendar.NOVEMBER, 12, 10, 30).getTime();
        Date expectedDateTime = new GregorianCalendar(2017, Calendar.NOVEMBER, 18, 23, 59, 59).getTime();

        Date weekEndDate = DateUtils.getWeekEndDate(mondayDate);

        assertEquals(expectedDateTime, weekEndDate);
    }

    @Test
    public void getWeekEndDate_whenWednesdayTest() throws Exception {
        Date wednesdayDate = new GregorianCalendar(2017, Calendar.NOVEMBER, 15, 10, 30).getTime();
        Date expectedDateTime = new GregorianCalendar(2017, Calendar.NOVEMBER, 18, 23, 59, 59).getTime();

        Date weekEndDate = DateUtils.getWeekEndDate(wednesdayDate);

        assertEquals(expectedDateTime, weekEndDate);
    }

    @Test
    public void getWeekEndDate_whenSaturdayTest() throws Exception {
        Date saturdayDate = new GregorianCalendar(2017, Calendar.NOVEMBER, 18, 10, 30).getTime();
        Date expectedDateTime = new GregorianCalendar(2017, Calendar.NOVEMBER, 18, 23, 59, 59).getTime();

        Date weekEndDate = DateUtils.getWeekEndDate(saturdayDate);

        assertEquals(expectedDateTime, weekEndDate);
    }

    @Test
    public void getNextWeekStartDate_whenMondayTest() throws Exception {
        Date mondayDate = new GregorianCalendar(2017, Calendar.NOVEMBER, 12, 10, 30).getTime();
        Date expectedDateTime = new GregorianCalendar(2017, Calendar.NOVEMBER, 19).getTime();

        Date nextWeekStartDate = DateUtils.getNextWeekStartDate(mondayDate);

        assertEquals(expectedDateTime, nextWeekStartDate);
    }

    @Test
    public void getNextWeekStartDate_whenWednesdayTest() throws Exception {
        Date mondayDate = new GregorianCalendar(2017, Calendar.NOVEMBER, 15, 10, 30).getTime();
        Date expectedDateTime = new GregorianCalendar(2017, Calendar.NOVEMBER, 19).getTime();

        Date nextWeekStartDate = DateUtils.getNextWeekStartDate(mondayDate);

        assertEquals(expectedDateTime, nextWeekStartDate);
    }

    @Test
    public void getNextWeekStartDate_whenSaturdayTest() throws Exception {
        Date mondayDate = new GregorianCalendar(2017, Calendar.NOVEMBER, 18, 10, 30).getTime();
        Date expectedDateTime = new GregorianCalendar(2017, Calendar.NOVEMBER, 19).getTime();

        Date nextWeekStartDate = DateUtils.getNextWeekStartDate(mondayDate);

        assertEquals(expectedDateTime, nextWeekStartDate);
    }

    @Test
    public void getNextWeekEndDate_whenMondayTest() throws Exception {
        Date mondayDate = new GregorianCalendar(2017, Calendar.NOVEMBER, 12, 10, 30).getTime();
        Date expectedDateTime = new GregorianCalendar(2017, Calendar.NOVEMBER, 25, 23, 59, 59).getTime();

        Date nextWeekEndDate = DateUtils.getNextWeekEndDate(mondayDate);

        assertEquals(expectedDateTime, nextWeekEndDate);
    }

    @Test
    public void getNextWeekEndDate_whenWednesdayTest() throws Exception {
        Date mondayDate = new GregorianCalendar(2017, Calendar.NOVEMBER, 15, 10, 30).getTime();
        Date expectedDateTime = new GregorianCalendar(2017, Calendar.NOVEMBER, 25, 23, 59, 59).getTime();

        Date nextWeekEndDate = DateUtils.getNextWeekEndDate(mondayDate);

        assertEquals(expectedDateTime, nextWeekEndDate);
    }

    @Test
    public void getNextWeekEndDate_whenSundayTest() throws Exception {
        Date mondayDate = new GregorianCalendar(2017, Calendar.NOVEMBER, 18, 10, 30).getTime();
        Date expectedDateTime = new GregorianCalendar(2017, Calendar.NOVEMBER, 25, 23, 59, 59).getTime();

        Date nextWeekEndDate = DateUtils.getNextWeekEndDate(mondayDate);

        assertEquals(expectedDateTime, nextWeekEndDate);
    }

    @Test
    public void getPreviousWeekStartDate_whenMondayTest() throws Exception {
        Date mondayDate = new GregorianCalendar(2017, Calendar.NOVEMBER, 12, 10, 30).getTime();
        Date expectedDateTime = new GregorianCalendar(2017, Calendar.NOVEMBER, 5).getTime();

        Date previousWeekStartDate = DateUtils.getPreviousWeekStartDate(mondayDate);

        assertEquals(expectedDateTime, previousWeekStartDate);
    }

    @Test
    public void getPreviousWeekStartDate_whenWednesdayTest() throws Exception {
        Date mondayDate = new GregorianCalendar(2017, Calendar.NOVEMBER, 15, 10, 30).getTime();
        Date expectedDateTime = new GregorianCalendar(2017, Calendar.NOVEMBER, 5).getTime();

        Date previousWeekStartDate = DateUtils.getPreviousWeekStartDate(mondayDate);

        assertEquals(expectedDateTime, previousWeekStartDate);
    }

    @Test
    public void getPreviousWeekStartDate_whenSaturdayTest() throws Exception {
        Date mondayDate = new GregorianCalendar(2017, Calendar.NOVEMBER, 18, 10, 30).getTime();
        Date expectedDateTime = new GregorianCalendar(2017, Calendar.NOVEMBER, 5).getTime();

        Date previousWeekStartDate = DateUtils.getPreviousWeekStartDate(mondayDate);

        assertEquals(expectedDateTime, previousWeekStartDate);
    }

    @Test
    public void getPreviousWeekEndDate_whenMondayTest() throws Exception {
        Date mondayDate = new GregorianCalendar(2017, Calendar.NOVEMBER, 12, 10, 30).getTime();
        Date expectedDateTime = new GregorianCalendar(2017, Calendar.NOVEMBER, 11, 23, 59, 59).getTime();

        Date previousWeekEndDate = DateUtils.getPreviousWeekEndDate(mondayDate);

        assertEquals(expectedDateTime, previousWeekEndDate);
    }

    @Test
    public void getPreviousWeekEndDate_whenWednesdayTest() throws Exception {
        Date mondayDate = new GregorianCalendar(2017, Calendar.NOVEMBER, 15, 10, 30).getTime();
        Date expectedDateTime = new GregorianCalendar(2017, Calendar.NOVEMBER, 11, 23, 59, 59).getTime();

        Date previousWeekEndDate = DateUtils.getPreviousWeekEndDate(mondayDate);

        assertEquals(expectedDateTime, previousWeekEndDate);
    }

    @Test
    public void getPreviousWeekEndDate_whenSundayTest() throws Exception {
        Date mondayDate = new GregorianCalendar(2017, Calendar.NOVEMBER, 18, 10, 30).getTime();
        Date expectedDateTime = new GregorianCalendar(2017, Calendar.NOVEMBER, 11, 23, 59, 59).getTime();

        Date previousWeekEndDate = DateUtils.getPreviousWeekEndDate(mondayDate);

        assertEquals(expectedDateTime, previousWeekEndDate);
    }

}