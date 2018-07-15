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

import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.adapters.ImportCardRecyclerAdapter;
import ru.sergeykamyshov.weekplanner.model.Card;
import ru.sergeykamyshov.weekplanner.model.Task;
import ru.sergeykamyshov.weekplanner.utils.DateUtils;
import ru.sergeykamyshov.weekplanner.utils.SharedPreferencesUtils;

public class CardPicker extends DialogFragment implements OnCardClickListener {

    // Тип недели импорта: 0 - текущая, 1 - следующая, 2 - архив
    private int mWeekType = 0;
    // Признак импорта задачи
    private boolean mIsImportTask = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<Card> cards = getCards();
        if (cards.isEmpty()) {
            dismiss();
        }

        // Создаем разметку для диалога
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_import, null);

        TextView dialogTitle = view.findViewById(R.id.dialog_import_title);
        dialogTitle.setText(R.string.dialog_title_cards);

        // Создаем и настраиваем адаптер
        ImportCardRecyclerAdapter adapter = new ImportCardRecyclerAdapter();
        adapter.setData(cards);
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

    public List<Card> getCards() {
        Date today = new Date();
        Date weekStartDate = today;
        Date weekEndDate = today;
        switch (mWeekType) {
            case 0:
                weekStartDate = DateUtils.getWeekStartDate(today);
                weekEndDate = DateUtils.getWeekEndDate(today);
                break;
            case 1:
                weekStartDate = DateUtils.getNextWeekStartDate(today);
                weekEndDate = DateUtils.getNextWeekEndDate(today);
                break;
            case 2:
                weekStartDate = DateUtils.getPreviousWeekStartDate(today);
                weekEndDate = DateUtils.getPreviousWeekEndDate(today);
                break;
            default:
                weekStartDate = DateUtils.getWeekStartDate(today);
                weekEndDate = DateUtils.getWeekEndDate(today);
                break;
        }

        // Возвращаем список карточек отсортированные по указанной позиции
        return Realm.getDefaultInstance().where(Card.class)
                .between("creationDate", weekStartDate, weekEndDate)
                .sort("position")
                .findAll();
    }

    @Override
    public void onCardClick(Card card) {
        if (mIsImportTask) {
            createAndShowTaskPicker(card);
        } else {
            copyAndUpdateCard(card);
        }
    }

    private void createAndShowTaskPicker(Card card) {
        if (card.getTasks().isEmpty()) {
            dismiss();
        }
        TaskPicker taskPicker = (TaskPicker) DialogFactory.getTaskPicker();
        taskPicker.setImportCardId(card.getId());
        taskPicker.show(getActivity().getSupportFragmentManager(), null);
        dismiss();
    }

    private void copyAndUpdateCard(Card card) {
        // Копируем карточку
        Realm realm = Realm.getDefaultInstance();
        Card cardCopy = realm.copyFromRealm(card);
        cardCopy.setId(UUID.randomUUID().toString());
        // Устанавливаем дату недели на которой импортируется карточка
        cardCopy.setCreationDate(SharedPreferencesUtils.getWeekEndDate(getActivity()));

        // Копируем задачи в карточке
        RealmList<Task> tasks = new RealmList<>();
        for (Task task : card.getTasks()) {
            Task taskCopy = realm.copyFromRealm(task);
            taskCopy.setId(UUID.randomUUID().toString());
            tasks.add(taskCopy);
        }
        cardCopy.setTasks(tasks);

        // Сохраняем копию
        realm.beginTransaction();
        realm.insert(cardCopy);
        realm.commitTransaction();

        dismiss();
        // Просим фрагмент обновить список карточек
        getActivity().getSupportFragmentManager().getFragments().get(0).onResume();
    }

    public void setWeekType(int weekType) {
        mWeekType = weekType;
    }

    public void setImportTask(boolean importTask) {
        mIsImportTask = importTask;
    }
}
