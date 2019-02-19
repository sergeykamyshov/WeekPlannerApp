package ru.sergeykamyshov.weekplanner.ui.dialogs.imports.tasks;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.data.db.model.Task;

public class ImportTaskRecyclerAdapter extends RecyclerView.Adapter<ImportTaskRecyclerAdapter.ViewHolder> {

    private List<Task> mData = new ArrayList<>();
    private List<Integer> mSelectedPositions = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_item_import_task, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (mSelectedPositions.contains(position)) {
                    mSelectedPositions.remove(Integer.valueOf(position));
                } else {
                    mSelectedPositions.add(position);
                }
                notifyItemChanged(position);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (mSelectedPositions.contains(position)) {
            holder.itemView.setBackgroundColor(Color.parseColor("#CFD8DC"));
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
        holder.title.setText(mData.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public List<Integer> getSelectedPositions() {
        return mSelectedPositions;
    }

    public void setData(List<Task> data) {
        mData = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txt_card_title);
        }
    }
}
