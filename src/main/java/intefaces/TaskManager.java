package main.java.intefaces;

import main.java.service.Status;
import main.java.service.TaskType;
import main.java.tasks.Task;

import java.util.List;
import java.util.UUID;

public interface TaskManager {
    void addNewTask(Task task);

    // case 2: Получение списка всех задач.-------------------------------------
    List<Task> getAllTasksByTaskType(TaskType taskType);

    void removeTasksByTasktype(TaskType taskType);

    void getTaskById(UUID taskId);

    void updateTask(Task task);

    void removeTaskById(UUID id);

    void changeStatusTask(UUID id, Status status);


    List<Task> getSubtaskList(UUID epicId);

    void updateEpicStatus(UUID epicId);

    List<Task> getHistory();
}