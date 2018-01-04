package ru.sergeykamyshov.weekplanner.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmList;
import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.activities.OnTaskItemClickListener;
import ru.sergeykamyshov.weekplanner.model.Task;
import ru.sergeykamyshov.weekplanner.utils.TaskItemTouchHelperAdapter;

public class CardRecyclerAdapter extends RecyclerView.Adapter<CardRecyclerAdapter.ViewHolder>
        implements TaskItemTouchHelperAdapter {

    private Context mContext;
    private RealmList<Task> mTasks;
    private OnTaskItemClickListener mOnTaskItemClickListener;

    public CardRecyclerAdapter(Context context, RealmList<Task> tasks, OnTaskItemClickListener onTaskItemClickListener) {
        mContext = context;
        mTasks = tasks;
        mOnTaskItemClickListener = onTaskItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_recycler_item, parent, false);
        return new CardRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.bind(mTasks.get(position), mOnTaskItemClickListener);
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    /**
     * Перемещает выбранную задачу в списке задач из позиции fromPosition на позицию toPosition
     *
     * @param fromPosition - позиция в списке откуда переместить
     * @param toPosition   - позиция в списке куда переместить
     */
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Task movedTask = mTasks.remove(fromPosition);
        mTasks.add(toPosition, movedTask);
        realm.commitTransaction();

        notifyItemMoved(fromPosition, toPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox mIsDone;
        TextView mTaskTitle;
        ImageView mMoreImg;

        public ViewHolder(View itemView) {
            super(itemView);
            mIsDone = itemView.findViewById(R.id.cb_is_done);
            mTaskTitle = itemView.findViewById(R.id.txt_task_title);
            mMoreImg = itemView.findViewById(R.id.img_more);
        }

        public void bind(final Task task, OnTaskItemClickListener onTaskItemClickListener) {
            mIsDone.setChecked(task.isDone());
            mIsDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mTasks.get(getAdapterPosition()).setDone(isChecked);
                        }
                    });
                }
            });

            mTaskTitle.setText(task.getTitle());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnTaskItemClickListener.onClick(task);
                }
            });
        }
    }
}
