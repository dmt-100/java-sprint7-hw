package main.java.intefaces;

import main.java.service.Status;
import main.java.service.TaskType;
import main.java.tasks.Task;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface TaskManager {
    void addNewTask(Task task);

    // case 2: Получение списка всех задач.-------------------------------------
    List<Task> getAllTasksByTaskType(TaskType taskType);

    void removeTasksByTasktype(TaskType taskType);

    Task getTask(UUID taskId);

    void updateTask(Task task);

    short removeTaskById(UUID id);

    void changeStatusTask(UUID id, Status status);


    List<Task> getSubtasksFromEpic(UUID epicId);

    void updateEpicStatus(UUID epicId);

    List<Task> getHistory();

    Map<UUID, Task> getTasks();

    // case 11:
    void prioritizeTasks();

    Set<Task> getPrioritizedTasks();

//    List<Task> sortByValue();
}