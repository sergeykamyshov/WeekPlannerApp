package ru.sergeykamyshov.weekplanner.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import ru.sergeykamyshov.weekplanner.R;

public class WeekPicker extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.action_import_weeks, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CardPicker cardPicker = (CardPicker) DialogFactory.getCardPicker();
                switch (which) {
                    case 0:
                        cardPicker.setWeekType(0);
                        break;
                    case 1:
                        cardPicker.setWeekType(1);
                        break;
                    case 2:
                        cardPicker.setWeekType(2);
                        break;
                    default:
                        cardPicker.setWeekType(0);
                        break;
                }
                cardPicker.show(getActivity().getSupportFragmentManager(), null);
            }
        });
        return builder.create();
    }
}
