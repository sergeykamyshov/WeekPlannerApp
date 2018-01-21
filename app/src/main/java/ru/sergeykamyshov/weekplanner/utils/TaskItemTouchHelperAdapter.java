package ru.sergeykamyshov.weekplanner.utils;

public interface TaskItemTouchHelperAdapter {

    /**
     * Перемещает выбранную задачу в списке из позиции fromPosition на позицию toPosition
     *
     * @param fromPosition - позиция в списке откуда переместить
     * @param toPosition   - позиция в списке куда переместить
     */
    void onItemMove(int fromPosition, int toPosition);

    /**
     * Удаляем задачу по позиции
     *
     * @param position - позиция задачи в списке
     */
    void onItemDismiss(int position);
}
