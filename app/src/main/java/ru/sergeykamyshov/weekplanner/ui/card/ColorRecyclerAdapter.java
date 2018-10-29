package ru.sergeykamyshov.weekplanner.ui.card;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.ui.custom.CircleColorView;

public class ColorRecyclerAdapter extends RecyclerView.Adapter<ColorRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mColors;
    private View.OnClickListener mOnClickListener;

    public ColorRecyclerAdapter(Context context, List<String> colors, View.OnClickListener onClickListener) {
        mContext = context;
        mColors = colors;
        mOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_color, parent, false);
        return new ColorRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mColors.get(position), mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mColors.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleColorView mCircleView;

        ViewHolder(View itemView) {
            super(itemView);
            mCircleView = itemView.findViewById(R.id.v_circle);
        }

        void bind(String color, final View.OnClickListener onClickListener) {
            mCircleView.setCircleColor(Color.parseColor(color));
            mCircleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(v);
                }
            });
        }
    }
}
