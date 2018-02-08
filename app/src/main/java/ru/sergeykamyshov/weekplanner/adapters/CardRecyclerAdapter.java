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

public class CardRecyclerAdapter extends RecyclerView.Adapter<CardRecyclerAdapter.ViewHolder> {

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

    public void onItemMove(int fromPosition, int toPosition) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Task movedTask = mTasks.remove(fromPosition);
        mTasks.add(toPosition, movedTask);
        realm.commitTransaction();

        notifyItemMoved(fromPosition, toPosition);
    }

    public Task onItemDismiss(int position) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Task removedTask = mTasks.remove(position);
        realm.commitTransaction();

        notifyItemRemoved(position);
        return removedTask;
    }

    public void insertItemToPosition(Task task, int position) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        mTasks.add(position, task);
        realm.commitTransaction();

        notifyItemInserted(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox mIsDone;
        TextView mTaskTitle;
        ImageView mMoreImg;
        public View mViewForeground;
        public View mViewBackground;

        public ViewHolder(View itemView) {
            super(itemView);
            mIsDone = itemView.findViewById(R.id.cb_is_done);
            mTaskTitle = itemView.findViewById(R.id.txt_task_title);
            mMoreImg = itemView.findViewById(R.id.img_more);
            mViewForeground = itemView.findViewById(R.id.layout_task_foreground);
            mViewBackground = itemView.findViewById(R.id.layout_task_background);
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
                    mOnTaskItemClickListener.onClick(task, getAdapterPosition());
                }
            });
        }
    }
}
