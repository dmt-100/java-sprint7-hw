package main.java.managers;

import main.java.intefaces.TaskManager;
import main.java.service.Status;
import main.java.service.TaskType;
import main.java.tasks.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

 abstract class TaskManagerTest<T extends TaskManager> {
     InMemoryTaskManager taskManager;

    {
        taskManager = new InMemoryTaskManager();
    }

     Map<UUID, Task> tasks = new HashMap<>();

     LocalDateTime dateTimeTestTask1 = LocalDateTime.parse("2014-12-22T05:10:30");
     LocalDateTime dateTimeTestTask2 = LocalDateTime.parse("2014-12-22T05:00:30");
     LocalDateTime dateTimeTestEpic1 = LocalDateTime.parse("2015-12-22T08:15:30");
     LocalDateTime dateTimeTestSubtask1 = LocalDateTime.parse("2016-12-22T10:20:30");



    @Test
    void addNewTask() {
        Task task1 = new Task(
                TaskType.TASK,
                "Переезд",
                "Собрать коробки",
                Status.NEW,
                dateTimeTestTask1,
                50);
        taskManager.addNewTask(task1);
        final Task savedTask = taskManager.getTask(task1.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        final Map<UUID, Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(task1.getId()), "Задачи не совпадают.");
    }

    @Test
    public List<Task> getAllTasksByTaskType(TaskType taskType) {
        List<Task> list = tasks.entrySet().stream()
                .filter(t -> t.getValue().getTaskType().equals(taskType))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        return list;
    }
/*
Для каждого метода нужно проверить его работу:
  a. Со стандартным поведением.
  b. С пустым списком задач.
  c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
 */
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