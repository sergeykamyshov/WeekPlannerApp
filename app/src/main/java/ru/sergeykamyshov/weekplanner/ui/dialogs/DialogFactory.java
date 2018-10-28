package ru.sergeykamyshov.weekplanner.ui.dialogs;

import android.support.v4.app.DialogFragment;

import ru.sergeykamyshov.weekplanner.ui.dialogs.imports.cards.CardPicker;
import ru.sergeykamyshov.weekplanner.ui.dialogs.imports.tasks.TaskPicker;
import ru.sergeykamyshov.weekplanner.ui.dialogs.imports.weeks.WeekPicker;

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
