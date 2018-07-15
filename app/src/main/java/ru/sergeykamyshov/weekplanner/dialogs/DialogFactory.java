package ru.sergeykamyshov.weekplanner.dialogs;

import android.support.v4.app.DialogFragment;

public class DialogFactory {

    public static DialogFragment getWeekPicker() {
        return new WeekPicker();
    }

    public static DialogFragment getCardPicker() {
        return new CardPicker();
    }

    public static DialogFragment getTaskPicker() {
        return new TaskPicker();
    }
}
