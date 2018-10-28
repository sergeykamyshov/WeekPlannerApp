package ru.sergeykamyshov.weekplanner.ui.dialogs.imports.tasks;

import ru.sergeykamyshov.weekplanner.data.db.model.Task;

public interface OnTaskClickListener {

    void onTaskClick(Task task);

}
