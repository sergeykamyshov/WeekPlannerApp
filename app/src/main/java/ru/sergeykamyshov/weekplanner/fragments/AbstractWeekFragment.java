package ru.sergeykamyshov.weekplanner.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.adapters.WeekRecyclerAdapter;
import ru.sergeykamyshov.weekplanner.dialogs.DialogFactory;
import ru.sergeykamyshov.weekplanner.model.Card;
import ru.sergeykamyshov.weekplanner.utils.CardItemTouchHelper;
import ru.sergeykamyshov.weekplanner.data.prefs.SharedPreferencesUtils;
import ru.sergeykamyshov.weekplanner.ui.custom.EmptyRecyclerView;

public abstract class AbstractWeekFragment extends Fragment {

    protected WeekRecyclerAdapter mWeekRecyclerAdapter;
    protected Realm mRealm;
    protected Date mWeekStartDate;
    protected Date mWeekEndDate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Вычисляем дату начала и окончания недели
        Date today = new Date();
        mWeekStartDate = getWeekStartDate(today);
        mWeekEndDate = getWeekEndDate(today);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week, container, false);

        // Создаем и настраиваем адаптер
        EmptyRecyclerView recyclerView = view.findViewById(R.id.week_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setEmptyView(view.findViewById(R.id.layout_empty_card_list));

        mWeekRecyclerAdapter = new WeekRecyclerAdapter(getContext(), Collections.<Card>emptyList());
        recyclerView.setAdapter(mWeekRecyclerAdapter);

        // Добавляем возможность перемещать карточки в списке
        ItemTouchHelper.Callback itemTouchCallback = new CardItemTouchHelper(mWeekRecyclerAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Нажатие на FloatingActionButton создает новую карточку на выбранной неделе
        FloatingActionButton fab = view.findViewById(R.id.fab_add_card);
        fab.setOnClickListener(getOnFabClickListener());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_week_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_import_card:
                FragmentActivity activity = getActivity();
                // Сохраняем дату окончания выбранной недели, чтобы далее сохранить карточку с этой датой
                SharedPreferencesUtils.saveWeekEndDate(activity, mWeekEndDate);
                DialogFactory.getWeekPicker().show(activity.getSupportFragmentManager(), null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    abstract Date getWeekStartDate(Date date);

    abstract Date getWeekEndDate(Date date);

    abstract View.OnClickListener getOnFabClickListener();

    @Override
    public void onResume() {
        super.onResume();

        // Обработка ошибки, для которой пока нет решения в интернете
        // Появляется преимущественно на версии Android 6.0 и смартфонах Samsung
        try {
            mRealm = Realm.getDefaultInstance();
        } catch (IllegalStateException e) {
            Realm.init(getActivity().getApplicationContext());
            mRealm = Realm.getDefaultInstance();
        }

        // Получаем список карточек отсортированные по указанной позиции
        RealmResults<Card> cards = mRealm.where(Card.class)
                .between("creationDate", mWeekStartDate, mWeekEndDate)
                .sort("position")
                .findAll();

        removeEmptyCards(cards);

        /**
         * Зачем заполнять поле "position" когда сортировка уже выполнена?
         * 1) В старых карточках позиция не была указана.
         * 2) Если пользователь сменит Local, то день начала недели изменится и может получится так,
         * что у карточек будут одинаковые позиции, а дальнейшее их перемещение только запутает все.
         * Для таких ситуаций мы перезаполним поля сразу, чтобы в дальнейшем этого не требовалось.
         */
        fillCardsPositions(cards);

        /**
         * Почему не используем RealmResults в адаптере?
         * RealmResults не позволяется динамически изменять список, т.к. там используются "живые объекты".
         * Динамическое изменение списка нам нужно чтобы перемещать карточки.
         */
        List<Card> copyFromRealmCards = mRealm.copyFromRealm(cards);
        mWeekRecyclerAdapter.setCards(copyFromRealmCards);
    }

    /**
     * Удаляет карточки у которых нет заголовка и отсутствуют задачи
     *
     * @param cards - список карточек
     */
    private void removeEmptyCards(RealmResults<Card> cards) {
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (TextUtils.isEmpty(card.getTitle()) && card.getTasks().isEmpty()) {
                mRealm.beginTransaction();
                card.deleteFromRealm();
                mRealm.commitTransaction();
            }
        }
    }

    /**
     * Заполняет поле "position" у карточек если их позиции в списке отличается от значения в поле.
     *
     * @param cards - список карточек
     */
    protected void fillCardsPositions(RealmResults<Card> cards) {
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (card.getPosition() != i) {
                mRealm.beginTransaction();
                card.setPosition(i);
                mRealm.commitTransaction();
            }
        }
    }
}
