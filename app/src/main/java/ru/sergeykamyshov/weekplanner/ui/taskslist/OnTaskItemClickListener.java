package ru.sergeykamyshov.weekplanner.ui.taskslist;

import ru.sergeykamyshov.weekplanner.data.db.model.Task;

public interface OnTaskItemClickListener {

    void onClick(Task task, int position);

}
