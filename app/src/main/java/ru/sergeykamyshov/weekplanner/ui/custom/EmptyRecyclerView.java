package ru.sergeykamyshov.weekplanner.ui.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Расширенный RecyclerView помогает установить View, которое показывается при отсутствии данных
 */
public class EmptyRecyclerView extends RecyclerView {

    private View mEmptyView;

    final AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            initEmptyView();
        }
    };

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Устанавливает видимость RecyclerView и EmptyView. Запускается каждый раз после изменения данных адаптера.
     */
    private void initEmptyView() {
        if (mEmptyView != null) {
            boolean isAdapterEmpty = getAdapter() == null || getAdapter().getItemCount() == 0;
            // Показываем указанное View
            mEmptyView.setVisibility(isAdapterEmpty ? VISIBLE : GONE);
            // Показываем сам RecyclerView
            setVisibility(isAdapterEmpty ? GONE : VISIBLE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(mObserver);
        }

        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
        }

        initEmptyView();
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        initEmptyView();
    }
}
