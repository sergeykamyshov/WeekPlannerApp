package ru.sergeykamyshov.weekplanner.ui.taskslist;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmList;
import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.data.db.model.Card;
import ru.sergeykamyshov.weekplanner.data.db.model.Task;
import ru.sergeykamyshov.weekplanner.utils.CardUtils;

public class CardRecyclerAdapter extends RecyclerView.Adapter<CardRecyclerAdapter.ViewHolder>
        implements TaskItemTouchHelperAdapter {

    private Context mContext;
    private String mCardId;
    private List<Task> mTasks;
    private Callback mCallback;
    private LayoutInflater mLayoutInflater;

    private boolean mSelectable = false;
    private Set<Task> mSelectedItems = new HashSet<>();

    CardRecyclerAdapter(Context context, String cardId, List<Task> tasks, Callback callback) {
        mContext = context;
        mCardId = cardId;
        mTasks = tasks;
        mCallback = callback;

        mLayoutInflater = LayoutInflater.from(context);
    }

    interface Callback {
        void onClick(Task task, int position);

        void onSelect();

        void onResetSelect();

        void taskChanged(Task task);

        void selectedTasksChanged(Set<Task> tasks);

        void onTaskDismiss(Task task, int position);

        void onDrag(RecyclerView.ViewHolder holder);
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.card_recycler_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.mIsDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Task task = mTasks.get(holder.getAdapterPosition());
                task.setDone(isChecked);

                mCallback.taskChanged(task);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                Task task = mTasks.get(adapterPosition);
                // Если уже есть выделенные элементы, то можно выделять простым кликом
                if (mSelectable) {
                    // Отменяем выделение если кликнули на уже выделенный
                    if (mSelectedItems.contains(task)) {
                        mSelectedItems.remove(task);
                        notifyItemChanged(adapterPosition);
                    } else {
                        mSelectedItems.add(task);
                        notifyItemChanged(adapterPosition);
                    }
                    // Сбрасываем признак выбора элементов если больше нет элементов
                    if (mSelectedItems.isEmpty()) {
                        cancelSelect();
                    }
                } else {
                    mCallback.onClick(task, adapterPosition);
                }
            }
        });

        holder.itemView.setLongClickable(true);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mSelectable) {
                    mSelectable = true;

                    int adapterPosition = holder.getAdapterPosition();
                    Task task = mTasks.get(adapterPosition);

                    mSelectedItems.add(task);
                    notifyItemChanged(adapterPosition);

                    mCallback.onSelect();
                }
                return true;
            }
        });

        holder.mDragImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mCallback.onDrag(holder);
                }
                return false;
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Task task = mTasks.get(position);
        if (task == null) {
            return;
        }

        holder.mIsDone.setChecked(task.isDone());

        holder.mTaskTitle.setText(task.getTitle());

        holder.mViewForeground.setBackgroundColor(
                mSelectedItems.contains(task)
                        ? Color.parseColor(CardUtils.getCardColor(mContext, mCardId))
                        : Color.WHITE
        );
    }

    void checkSelectedItems(boolean checked) {
        // После изменения checkbox, сработает listener и вызовет сохранение в базу (в методе onCreateViewHolder())
        for (Task task : mSelectedItems) {
            task.setDone(checked);
        }
        // В некоторых случаях checkbox listener перезаписывают друг друга, поэтому сохраняем каждый объект явно
        mCallback.selectedTasksChanged(mSelectedItems);
        cancelSelect();
    }

    void deleteSelectedItems() {
        mTasks.removeAll(mSelectedItems);
        cancelSelect();
    }

    void cancelSelect() {
        mSelectable = false;
        mSelectedItems.clear();
        notifyDataSetChanged();
        mCallback.onResetSelect();
    }

    boolean isSelectable() {
        return mSelectable;
    }

    void setTasks(List<Task> tasks) {
        mTasks = tasks;
        notifyDataSetChanged();
    }

    void insertItemToPosition(Task task, int position) {
        mTasks.add(position, task);
        notifyItemInserted(position);
    }

    Set<Task> getSelectedItems() {
        return mSelectedItems;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Realm realm = Realm.getDefaultInstance();

        Card card = realm.where(Card.class).equalTo("id", mCardId).findFirst();
        if (card == null) {
            return;
        }
        realm.beginTransaction();
        RealmList<Task> realmTasks = card.getTasks();
        Task removedRealmTask = realmTasks.remove(fromPosition);
        realmTasks.add(toPosition, removedRealmTask);
        realm.commitTransaction();

        Task removedTask = mTasks.remove(fromPosition);
        mTasks.add(toPosition, removedTask);

        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        Task task = mTasks.remove(position);
        mCallback.onTaskDismiss(task, position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemChecked(int position) {
        Task task = mTasks.get(position);
        task.setDone(!task.isDone());
        mCallback.taskChanged(task);
        notifyItemChanged(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox mIsDone;
        TextView mTaskTitle;
        ImageView mDragImg;
        View mViewForeground;
        View mViewBackgroundDone;
        View mViewBackgroundUndone;
        View mViewBackgroundRemove;

        ViewHolder(View itemView) {
            super(itemView);
            mIsDone = itemView.findViewById(R.id.cb_is_done);
            mTaskTitle = itemView.findViewById(R.id.txt_task_title);
            mDragImg = itemView.findViewById(R.id.img_drag);
            mViewForeground = itemView.findViewById(R.id.layout_task_foreground);
            mViewBackgroundDone = itemView.findViewById(R.id.layout_task_done_background);
            mViewBackgroundUndone = itemView.findViewById(R.id.layout_task_undone_background);
            mViewBackgroundRemove = itemView.findViewById(R.id.layout_task_remove_background);
        }
    }

}
