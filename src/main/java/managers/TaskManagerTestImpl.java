package main.java.managers;

import main.java.intefaces.TaskManager;
import main.java.service.Status;
import main.java.service.TaskType;
import main.java.tasks.Task;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskManagerTestImpl<T extends TaskManager> extends TaskManagerTest<T> {
    InMemoryTaskManager taskManager;

    {
        taskManager = new InMemoryTaskManager();
    }


    /*
    Для каждого метода нужно проверить его работу:
  a. Со стандартным поведением.
  b. С пустым списком задач.
  c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
     */

    @Test
    void addNewTask() {
        Task task = new Task(UUID.randomUUID(), TaskType.TASK, "Переезд", "Собрать коробки", Status.NEW);
        final UUID taskId = taskManager.addNewTask(task);
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final Map<UUID, Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(task.getId()), "Задачи не совпадают.");

        taskManager.getTasks().clear();
    }
/*
Для подзадач нужно дополнительно проверить наличие эпика, а для эпика — расчёт статуса.
Для каждого метода нужно проверить его работу:
  a. Со стандартным поведением.
  b. С пустым списком задач.
  c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
 */

    @Test
    void removeTasksByTasktype() {

    }

    @Test
    void getTaskById() {
    }

    @Test
    void updateTask() {
    }

    @Test
    void removeTaskById() {
    }

    @Test
    void changeStatusTask() {
    }

    @Test
    void getSubtaskList() {
    }

    @Test
    void updateEpicStatus() {
    }

    @Test
    void getHistory() {
    }

    @Test
    void getTasks() {

    }
}