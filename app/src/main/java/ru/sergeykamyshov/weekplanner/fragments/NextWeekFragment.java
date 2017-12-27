package ru.sergeykamyshov.weekplanner.fragments;

import android.view.View;

import java.util.Date;

import ru.sergeykamyshov.weekplanner.utils.DateUtils;

public class NextWeekFragment extends AbstractWeekFragment {

    public static NextWeekFragment newInstance() {
        return new NextWeekFragment();
    }

    @Override
    Date getWeekStartDate(Date date) {
        return DateUtils.getNextWeekStartDate(date);
    }

    @Override
    Date getWeekEndDate(Date date) {
        return DateUtils.getNextWeekEndDate(date);
    }

    @Override
    View.OnClickListener getOnFabClickListener() {
        return null;
    }
}
