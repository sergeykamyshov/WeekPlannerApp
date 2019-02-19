package ru.sergeykamyshov.weekplanner.ui.dialogs.imports.tasks;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.data.db.model.Card;
import ru.sergeykamyshov.weekplanner.data.db.model.Task;
import ru.sergeykamyshov.weekplanner.data.prefs.SharedPreferencesUtils;
import ru.sergeykamyshov.weekplanner.ui.taskslist.CardActivity;

public class TaskPicker extends DialogFragment {

    // ID карточки из которой выбираем задачу для импорта
    private String mImportCardId = "";
    private ImportTaskRecyclerAdapter adapter;
    private List<Task> tasks = new ArrayList<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        tasks = getTasks();
        if (tasks.isEmpty()) {
            dismiss();
        }

        // Создаем разметку для диалога
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_import_tasks, null);

        TextView dialogTitle = view.findViewById(R.id.dialog_import_title);
        dialogTitle.setText(R.string.dialog_title_tasks);

        // Создаем и настраиваем адаптер
        adapter = new ImportTaskRecyclerAdapter();
        adapter.setData(tasks);

        // Создаем и настраиваем RecyclerView
        RecyclerView recycler = view.findViewById(R.id.recycler_import);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(adapter);

        // Создаем диалог с кастомным View
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setNegativeButton(getString(R.string.common_action_cancel), null)
                .setPositiveButton(R.string.common_action_import, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        importTasks();
                    }
                });
        builder.setView(view);
        return builder.create();
    }

    private void importTasks() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        // Загружаем карточку, в которую необходимо импортировать задачу
        String cardId = SharedPreferencesUtils.getCardIdForImportTask(getActivity());
        Card card = realm.where(Card.class).equalTo("id", cardId).findFirst();
        if (card == null) {
            return;
        }

        for (int position : adapter.getSelectedPositions()) {
            // Копируем задачу
            Task taskCopy = realm.copyFromRealm(tasks.get(position));
            taskCopy.setId(UUID.randomUUID().toString());
            card.addTask(taskCopy);
        }
        realm.insertOrUpdate(card);
        realm.commitTransaction();

        // Просим обновить список задач
        ((CardActivity) getActivity()).updateList();

        dismiss();
    }

    public List<Task> getTasks() {
        Card card = Realm.getDefaultInstance().where(Card.class)
                .equalTo("id", mImportCardId)
                .findFirst();
        if (card == null) {
            return new ArrayList<>();
        }
        return card.getTasks();
    }

    public void setImportCardId(String cardId) {
        mImportCardId = cardId;
    }
}
