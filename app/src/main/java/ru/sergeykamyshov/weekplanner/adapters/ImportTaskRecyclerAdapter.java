package ru.sergeykamyshov.weekplanner.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.dialogs.OnTaskClickListener;
import ru.sergeykamyshov.weekplanner.model.Task;

public class ImportTaskRecyclerAdapter extends RecyclerView.Adapter<ImportTaskRecyclerAdapter.ViewHolder> {

    private List<Task> mData = new ArrayList<>();
    private OnTaskClickListener mListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_item_import_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.title.setText(mData.get(position).getTitle());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onTaskClick(mData.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<Task> data) {
        mData = data;
    }

    public void setListener(OnTaskClickListener listener) {
        mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewGroup container;
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container_card_title);
            title = itemView.findViewById(R.id.txt_card_title);
        }
    }
}
