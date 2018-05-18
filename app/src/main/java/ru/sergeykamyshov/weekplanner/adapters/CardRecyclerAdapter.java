package ru.sergeykamyshov.weekplanner.adapters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
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
import ru.sergeykamyshov.weekplanner.utils.TaskSharedPreferencesUtils;

public class CardRecyclerAdapter extends RecyclerView.Adapter<CardRecyclerAdapter.ViewHolder> implements DataView {

    private Context mContext;
    private RealmList<Task> mTasks;
    private OnTaskItemClickListener mOnTaskItemClickListener;
    private TaskSharedPreferencesUtils mPreferencesUtils;

    public CardRecyclerAdapter(Context context, RealmList<Task> tasks, OnTaskItemClickListener onTaskItemClickListener) {
        mContext = context;
        mTasks = tasks;
        mOnTaskItemClickListener = onTaskItemClickListener;
        mPreferencesUtils = new TaskSharedPreferencesUtils((AppCompatActivity) mContext);
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

    @Override
    public void refresh() {
        notifyDataSetChanged();
    }

    public void onItemMove(int fromPosition, int toPosition) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Task movedTask = mTasks.remove(fromPosition);
        mTasks.add(toPosition, movedTask);
        realm.commitTransaction();

        notifyItemMoved(fromPosition, toPosition);
    }

    public void onItemDismiss(int position) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Task removedTask = mTasks.remove(position);
        mPreferencesUtils.saveData("", removedTask, position);
        if (removedTask.isValid()) {
            removedTask.deleteFromRealm();
        }
        realm.commitTransaction();

        notifyItemRemoved(position);
    }

    public void onItemChecked(int position) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Task task = mTasks.get(position);
        if (task != null) {
            boolean checked = task.isDone();
            task.setDone(!checked);
        }
        realm.commitTransaction();

        notifyItemChanged(position);
    }

    public void insertItemToPosition(Task task, int position) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        mTasks.add(position, task);
        realm.commitTransaction();

        notifyItemInserted(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CheckBox mIsDone;
        TextView mTaskTitle;
        ImageView mMoreImg;
        public View mViewForeground;
        public View mViewBackgroundDone;
        public View mViewBackgroundUndone;
        public View mViewBackgroundRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            mIsDone = itemView.findViewById(R.id.cb_is_done);
            mTaskTitle = itemView.findViewById(R.id.txt_task_title);
            mMoreImg = itemView.findViewById(R.id.img_more);
            mViewForeground = itemView.findViewById(R.id.layout_task_foreground);
            mViewBackgroundDone = itemView.findViewById(R.id.layout_task_done_background);
            mViewBackgroundUndone = itemView.findViewById(R.id.layout_task_undone_background);
            mViewBackgroundRemove = itemView.findViewById(R.id.layout_task_remove_background);
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
