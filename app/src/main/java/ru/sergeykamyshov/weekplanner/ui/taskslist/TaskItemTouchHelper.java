package ru.sergeykamyshov.weekplanner.ui.taskslist;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

public class TaskItemTouchHelper extends ItemTouchHelper.Callback {

    private TaskItemTouchHelperAdapter mAdapter;

    public TaskItemTouchHelper(TaskItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        mAdapter.onItemMove(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.START) {
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        } else if (direction == ItemTouchHelper.END) {
            mAdapter.onItemChecked(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            CardRecyclerAdapter.ViewHolder recyclerViewHolder = (CardRecyclerAdapter.ViewHolder) viewHolder;

            // Если сдвигаем справа налево, то показываем что удаляем задачу
            View backgroundRemove = recyclerViewHolder.mViewBackgroundRemove;
            backgroundRemove.setVisibility(dX < 0 ? View.VISIBLE : View.INVISIBLE);

            // Если сдвигаем слева направо, то показываем что меняем статус задачи
            boolean taskDone = recyclerViewHolder.mIsDone.isChecked();
            View backgroundDone = recyclerViewHolder.mViewBackgroundDone;
            View backgroundUndone = recyclerViewHolder.mViewBackgroundUndone;
            backgroundDone.setVisibility(dX > 0 && !taskDone ? View.VISIBLE : View.INVISIBLE);
            backgroundUndone.setVisibility(dX > 0 && taskDone ? View.VISIBLE : View.INVISIBLE);

            View foregroundView = recyclerViewHolder.mViewForeground;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        CardRecyclerAdapter.ViewHolder recyclerViewHolder = (CardRecyclerAdapter.ViewHolder) viewHolder;
        View foregroundView = recyclerViewHolder.mViewForeground;
        getDefaultUIUtil().clearView(foregroundView);
    }
}
