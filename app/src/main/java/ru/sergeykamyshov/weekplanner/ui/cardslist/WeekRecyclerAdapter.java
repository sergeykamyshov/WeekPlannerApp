package ru.sergeykamyshov.weekplanner.ui.cardslist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.data.db.model.Card;
import ru.sergeykamyshov.weekplanner.data.db.model.Task;
import ru.sergeykamyshov.weekplanner.ui.taskslist.CardActivity;
import ru.sergeykamyshov.weekplanner.utils.CardUtils;
import ru.sergeykamyshov.weekplanner.utils.Const;

import static ru.sergeykamyshov.weekplanner.ui.taskslist.CardActivity.EXTRA_CARD_ID;

public class WeekRecyclerAdapter extends RecyclerView.Adapter<WeekRecyclerAdapter.ViewHolder>
        implements CardItemTouchHelperAdapter {

    private Context mContext;
    private List<Card> mCards = new ArrayList<>();
    private int mViewType;

    public WeekRecyclerAdapter(Context context, int viewType) {
        mContext = context;
        mViewType = viewType;
    }

    public void setCards(List<Card> cards) {
        mCards = cards;
        notifyDataSetChanged();
    }

    public void setViewType(int viewType) {
        mViewType = viewType;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.week_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Card card = mCards.get(position);
        if (card == null) {
            return;
        }

        String title = card.getTitle();
        if (TextUtils.isEmpty(title)) {
            holder.mCardTitle.setVisibility(View.GONE);
        } else {
            holder.mCardTitle.setText(title);
            holder.mCardTitle.setVisibility(View.VISIBLE);
        }

        String color = CardUtils.getCardColor(mContext, card.getId());
        holder.mCardColor.setBackgroundColor(Color.parseColor(color));

        // Очищаем список задач для карточки перед заполнением
        holder.mRecyclerItemLayout.removeAllViews();
        if (mViewType == Const.VIEW_CARDS) {
            if (card.getTasks() != null && !card.getTasks().isEmpty()) {
                LinearLayout linearLayout = createTasksLayout(card);
                holder.mRecyclerItemLayout.addView(linearLayout);
                holder.mRecyclerItemLayout.setVisibility(View.VISIBLE);
            }
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

        TextView title = view.findViewById(R.id.txt_task_title);
        title.setText(task.getTitle());

        if (task.isDone()) {
            title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            title.setTextColor(mContext.getResources().getColor(R.color.card_task_done_text_strike));
        }

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
        View mCardColor;
        LinearLayout mRecyclerItemLayout;

        ViewHolder(View view) {
            super(view);
            mCardView = view.findViewById(R.id.recycler_card_view);
            mCardTitle = view.findViewById(R.id.txt_card_title);
            mCardColor = view.findViewById(R.id.v_card_title_color);
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
