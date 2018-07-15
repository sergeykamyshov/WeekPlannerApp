package ru.sergeykamyshov.weekplanner.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ru.sergeykamyshov.weekplanner.R;

public class WeekPicker extends DialogFragment implements View.OnClickListener {

    // Признак импорта задачи
    private boolean mIsImportTask = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Создаем разметку для диалога
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_import_weeks, null);

        // Устанавливаем обработчики нажатия
        TextView currectWeek = view.findViewById(R.id.txt_current_week);
        currectWeek.setOnClickListener(this);
        TextView nextWeek = view.findViewById(R.id.txt_next_week);
        nextWeek.setOnClickListener(this);
        TextView archive = view.findViewById(R.id.txt_archive);
        archive.setOnClickListener(this);

        // Создаем и устанавливаем кастомную разметку для диалога
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View view) {
        CardPicker cardPicker = (CardPicker) DialogFactory.getCardPicker();
        switch (view.getId()) {
            case R.id.txt_current_week:
                cardPicker.setWeekType(0);
                break;
            case R.id.txt_next_week:
                cardPicker.setWeekType(1);
                break;
            case R.id.txt_archive:
                cardPicker.setWeekType(2);
                break;
            default:
                cardPicker.setWeekType(0);
                break;
        }
        cardPicker.setImportTask(mIsImportTask);
        cardPicker.show(getActivity().getSupportFragmentManager(), null);

        dismiss();
    }

    public void setImportTask(boolean isImportTask) {
        mIsImportTask = isImportTask;
    }
}
