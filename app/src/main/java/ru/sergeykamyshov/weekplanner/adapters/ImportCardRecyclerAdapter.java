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
import ru.sergeykamyshov.weekplanner.dialogs.OnCardClickListener;
import ru.sergeykamyshov.weekplanner.model.Card;

public class ImportCardRecyclerAdapter extends RecyclerView.Adapter<ImportCardRecyclerAdapter.ViewHolder> {

    private List<Card> mData = new ArrayList<>();
    private OnCardClickListener mListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_item_import_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.cardTitle.setText(mData.get(position).getTitle());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(mData.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<Card> data) {
        mData = data;
    }

    public void setListener(OnCardClickListener listener) {
        mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewGroup container;
        TextView cardTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container_card_title);
            cardTitle = itemView.findViewById(R.id.txt_card_title);
        }
    }
}
