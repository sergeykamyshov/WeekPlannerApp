package ru.sergeykamyshov.weekplanner.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.adapters.WeekRecyclerAdapter;
import ru.sergeykamyshov.weekplanner.model.Card;
import ru.sergeykamyshov.weekplanner.views.EmptyRecyclerView;

public abstract class AbstractWeekFragment extends Fragment {

    protected WeekRecyclerAdapter mWeekRecyclerAdapter;
    protected Realm mRealm;
    protected Date mWeekStartDate;
    protected Date mWeekEndDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week, container, false);

        // Создаем и настраиваем адаптер
        EmptyRecyclerView recyclerView = view.findViewById(R.id.week_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setEmptyView(view.findViewById(R.id.layout_empty_card_list));

        mRealm = Realm.getDefaultInstance();
        // Показываем неделю по умолчанию
        Date today = new Date();
        mWeekStartDate = getWeekStartDate(today);
        mWeekEndDate = getWeekEndDate(today);
        RealmResults<Card> cards = mRealm.where(Card.class)
                .between("creationDate", mWeekStartDate, mWeekEndDate)
                .findAll();

        mWeekRecyclerAdapter = new WeekRecyclerAdapter(getContext(), cards);
        recyclerView.setAdapter(mWeekRecyclerAdapter);

        // Нажатие на FloatingActionButton создает новую карточку на выбранной неделе
        FloatingActionButton fab = view.findViewById(R.id.fab_add_card);
        fab.setOnClickListener(getOnFabClickListener());

        return view;
    }

    abstract Date getWeekStartDate(Date date);

    abstract Date getWeekEndDate(Date date);

    abstract View.OnClickListener getOnFabClickListener();
}
