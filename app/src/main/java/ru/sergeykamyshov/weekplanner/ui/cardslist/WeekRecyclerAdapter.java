package ru.sergeykamyshov.weekplanner.ui.cardslist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import io.realm.Realm;
import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.data.db.model.Card;
import ru.sergeykamyshov.weekplanner.data.db.model.Task;
import ru.sergeykamyshov.weekplanner.ui.taskslist.CardActivity;

import static ru.sergeykamyshov.weekplanner.ui.taskslist.CardActivity.EXTRA_CARD_ID;

public class WeekRecyclerAdapter extends RecyclerView.Adapter<WeekRecyclerAdapter.ViewHolder>
        implements CardItemTouchHelperAdapter {

    private Context mContext;
    private List<Card> mCards;

    public WeekRecyclerAdapter(Context context, List<Card> cards) {
        mContext = context;
        mCards = cards;
    }

    public void setCards(List<Card> cards) {
        mCards = cards;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.week_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Card card = mCards.get(position);
        String cardTitle = card != null ? card.getTitle() : "";
        if (TextUtils.isEmpty(cardTitle)) {
            holder.mCardTitle.setVisibility(View.GONE);
        } else {
            holder.mCardTitle.setText(cardTitle);
            holder.mCardTitle.setVisibility(View.VISIBLE);
        }

        // TODO: delete after
        if (position % 4 == 0) {
            holder.mCardTitleColor.setBackgroundColor(mContext.getResources().getColor(R.color.card_title_4));
        } else if (position % 3 == 0 || position == 1) {
            holder.mCardTitleColor.setBackgroundColor(mContext.getResources().getColor(R.color.card_title_3));
        } else if (position % 2 == 0) {
            holder.mCardTitleColor.setBackgroundColor(mContext.getResources().getColor(R.color.card_title_2));
        } else {
            holder.mCardTitleColor.setBackgroundColor(mContext.getResources().getColor(R.color.card_title_1));
        }

        // Очищаем список задач для карточки перед заполнением
        holder.mRecyclerItemLayout.removeAllViews();
        if (card.getTasks() != null && !card.getTasks().isEmpty()) {
            LinearLayout linearLayout = createTasksLayout(card);
            holder.mRecyclerItemLayout.addView(linearLayout);
            holder.mRecyclerItemLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    private LinearLayout createTasksLayout(Card card) {
        LinearLayout linearLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        for (Task task : card.getTasks()) {
            createTaskItemLayout(linearLayout, task);
        }
        return linearLayout;
    }

    private void createTaskItemLayout(LinearLayout linearLayout, Task task) {
        View view = LayoutInflater.from(linearLayout.getContext()).inflate(R.layout.week_card_task_item, linearLayout, false);
        CheckBox taskDone = view.findViewById(R.id.cb_task_done);
        taskDone.setChecked(task.isDone());
        TextView taskTitle = view.findViewById(R.id.txt_task_title);
        taskTitle.setText(task.getTitle());
        linearLayout.addView(view);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        // Удалем выбранную карточку из списка
        Card movedCard = mCards.remove(fromPosition);
        // Устанавливаем ей новую позицию
        movedCard.setPosition(toPosition);
        // Сохраняем изменения
        realm.insertOrUpdate(movedCard);
        // Добавляем в список на новую позицию
        mCards.add(toPosition, movedCard);

        // Получаем карту, которая были сдвинута
        Card otherCard = mCards.get(fromPosition);
        // Устанавливаем ей новую позицию
        otherCard.setPosition(fromPosition);
        // Сохраняем изменения
        realm.insertOrUpdate(otherCard);
        realm.commitTransaction();

        notifyItemMoved(fromPosition, toPosition);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView mCardView;
        TextView mCardTitle;
        View mCardTitleColor;
        LinearLayout mRecyclerItemLayout;

        ViewHolder(View view) {
            super(view);
            mCardView = view.findViewById(R.id.recycler_card_view);
            mCardTitle = view.findViewById(R.id.txt_card_title);
            mCardTitleColor = view.findViewById(R.id.v_card_title_color);
            mRecyclerItemLayout = view.findViewById(R.id.list_tasks);

            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, CardActivity.class);
                    intent.putExtra(EXTRA_CARD_ID, mCards.get(getAdapterPosition()).getId());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
