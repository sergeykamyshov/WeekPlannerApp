package ru.sergeykamyshov.weekplanner.ui.dialogs.imports.cards;

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
import ru.sergeykamyshov.weekplanner.data.db.model.Card;

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
        Card card = mData.get(position);
        holder.cardColor.setBackgroundColor(Color.parseColor(card.getColor()));
        holder.cardTitle.setText(card.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCardClick(mData.get(holder.getAdapterPosition()));
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
        View cardColor;
        TextView cardTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            cardColor = itemView.findViewById(R.id.v_card_color);
            cardTitle = itemView.findViewById(R.id.txt_card_title);
        }
    }
}
