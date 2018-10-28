package ru.sergeykamyshov.weekplanner.dialogs;

import android.app.Dialog;
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
import ru.sergeykamyshov.weekplanner.activities.CardActivity;
import ru.sergeykamyshov.weekplanner.adapters.ImportTaskRecyclerAdapter;
import ru.sergeykamyshov.weekplanner.model.Card;
import ru.sergeykamyshov.weekplanner.model.Task;
import ru.sergeykamyshov.weekplanner.data.prefs.SharedPreferencesUtils;

public class TaskPicker extends DialogFragment implements OnTaskClickListener {

    // ID карточки из которой выбираем задачу для импорта
    private String mImportCardId = "";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<Task> tasks = getTasks();
        if (tasks.isEmpty()) {
            dismiss();
        }

        // Создаем разметку для диалога
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_import, null);

        TextView dialogTitle = view.findViewById(R.id.dialog_import_title);
        dialogTitle.setText(R.string.dialog_title_tasks);

        // Создаем и настраиваем адаптер
        ImportTaskRecyclerAdapter adapter = new ImportTaskRecyclerAdapter();
        adapter.setData(tasks);
        adapter.setListener(this);

        // Создаем и настраиваем RecyclerView
        RecyclerView recycler = view.findViewById(R.id.recycler_import);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(adapter);

        // Создаем диалог с кастомным View
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onTaskClick(Task task) {
        Realm realm = Realm.getDefaultInstance();
        // Копируем задачу
        Task taskCopy = realm.copyFromRealm(task);
        taskCopy.setId(UUID.randomUUID().toString());

        // Загружаем карточку, в которую необходимо импортировать задачу
        String cardId = SharedPreferencesUtils.getCardIdForImportTask(getActivity());
        Card card = realm.where(Card.class).equalTo("id", cardId).findFirst();
        if (card == null) {
            return;
        }
        realm.beginTransaction();
        card.addTask(taskCopy);
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
