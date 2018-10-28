package ru.sergeykamyshov.weekplanner.ui.cardslist.current;

import android.content.Intent;
import android.view.View;

import java.util.Date;

import ru.sergeykamyshov.weekplanner.ui.card.CardTitleActivity;
import ru.sergeykamyshov.weekplanner.ui.cardslist.AbstractWeekFragment;
import ru.sergeykamyshov.weekplanner.utils.DateUtils;

import static ru.sergeykamyshov.weekplanner.ui.taskslist.CardActivity.EXTRA_NEW_CARD_FLAG;
import static ru.sergeykamyshov.weekplanner.ui.taskslist.CardActivity.EXTRA_POSITION;
import static ru.sergeykamyshov.weekplanner.ui.taskslist.CardActivity.EXTRA_WEEK_END_DATE;

public class CurrentWeekFragment extends AbstractWeekFragment {

    public static CurrentWeekFragment newInstance() {
        return new CurrentWeekFragment();
    }

    @Override
    public Date getWeekStartDate(Date date) {
        return DateUtils.getWeekStartDate(date);
    }

    @Override
    public Date getWeekEndDate(Date date) {
        return DateUtils.getWeekEndDate(date);
    }

    @Override
    public View.OnClickListener getOnFabClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переходим к созданию новой карточки
                Intent intent = new Intent(getContext(), CardTitleActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra(EXTRA_NEW_CARD_FLAG, true);
                intent.putExtra(EXTRA_POSITION, mWeekRecyclerAdapter.getItemCount());
                intent.putExtra(EXTRA_WEEK_END_DATE, mWeekEndDate);
                getContext().startActivity(intent);
            }
        };
    }
}
