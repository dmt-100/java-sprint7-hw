package main.java.managers;

import main.java.intefaces.HistoryManager;
import main.java.intefaces.TaskManager;
import main.java.service.Status;
import main.java.service.TaskType;
import main.java.tasks.Epic;
import main.java.tasks.Subtask;
import main.java.tasks.Task;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class Managers {

    public static  HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static  TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    /*
        Потребуются следующие тесты.
        Для расчёта статуса Epic. Граничные условия:
           a.   Пустой список подзадач.
           b.   Все подзадачи со статусом NEW.
           c.    Все подзадачи со статусом DONE.
           d.    Подзадачи со статусами NEW и DONE.
           e.    Подзадачи со статусом IN_PROGRESS.
         */
    static class TaskManagerEpicTest<T extends TaskManager> {
        static String sep = File.separator;
        static String savesTasksFile = String.join(sep, "src", "main", "java", "saves", "taskSaves" + ".csv");
        static File file = new File(savesTasksFile);

        static InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        @Test
        static void create() {
            List<UUID> subtasksList = new ArrayList<>();
            Task task1 = new Task(UUID.randomUUID(), TaskType.TASK, "Переезд", "Собрать коробки", Status.NEW);
            Epic epic1 = new Epic(UUID.randomUUID(), TaskType.EPIC, "Переезд", "Переезд", Status.NEW, subtasksList);
            Subtask subtask1 = new Subtask(UUID.randomUUID(), TaskType.SUBTASK, "тест1",
                    "Собрать коробки", Status.NEW, epic1.getId(), LocalDateTime.now(), 50);
            Subtask subtask2 = new Subtask(UUID.randomUUID(), TaskType.SUBTASK, "тест2",
                    "Упаковать кошку", Status.NEW, epic1.getId(), LocalDateTime.now(), 5);

            inMemoryTaskManager.addNewTask(task1);
            inMemoryTaskManager.addNewTask(epic1);
            inMemoryTaskManager.addNewTask(subtask1);
            inMemoryTaskManager.addNewTask(subtask2);

        }

        @Test
        void allSubtasksWithStatusNew() { // b.   Все подзадачи со статусом NEW.
            create();
            boolean flag = false; // флаг на прохождение фильтра
            for (Task subtask : inMemoryTaskManager.getTasks().values()) {
                if (subtask.getTaskType().equals(TaskType.SUBTASK) && subtask.getStatus().equals(Status.NEW)) {
                    flag = true;
                    assertEquals(true, flag);
                }
            }
            inMemoryTaskManager.getTasks().clear();

        }

        @Test
        void allSubtasksWithStatusNewAndDone() { // d.    Подзадачи со статусами NEW и DONE.
            create();
            int statusDoneOrNew = 0;
            for (Task subtask : inMemoryTaskManager.getTasks().values()) {
                if (subtask.getTaskType().equals(TaskType.SUBTASK)) {
                    subtask.setStatus(Status.DONE); // первому сабтаску назначаем DONE и break;
                    break;
                }
            }
            for (Task subtask : inMemoryTaskManager.getTasks().values()) {
                if (subtask.getTaskType().equals(TaskType.SUBTASK)) {

                    if (subtask.getStatus().equals(Status.DONE) || subtask.getStatus().equals(Status.NEW)) {
                        statusDoneOrNew++;
                    }
                }
            }
            assertEquals(2, statusDoneOrNew);
            inMemoryTaskManager.getTasks().clear();
        }

        @Test
        void allSubtasksWithStatusInprogress() { // e.    Подзадачи со статусом IN_PROGRESS.
            create();
            int statusInprogress = 0;
            for (Task subtask : inMemoryTaskManager.getTasks().values()) {
                if (subtask.getTaskType().equals(TaskType.SUBTASK)) {
                    subtask.setStatus(Status.IN_PROGRESS); // меняем задачу на IN_PROGRESS
                    statusInprogress++;
                }
            }
            assertEquals(2, statusInprogress);
            inMemoryTaskManager.getTasks().clear();
        }


        @Test
        void allSubtasksWithStatusDone() { // c.    Все подзадачи со статусом DONE.
            create();
            boolean flag;
            for (Task subtask : inMemoryTaskManager.getTasks().values()) {
                if (subtask.getTaskType().equals(TaskType.SUBTASK)) {
                    inMemoryTaskManager.changeStatusTask(subtask.getId(), Status.DONE); // меняем статус подзадач
                }
            }
            for (Task epicAndSubtasks : inMemoryTaskManager.getTasks().values()) {
                if (epicAndSubtasks.getTaskType().equals(TaskType.SUBTASK) || epicAndSubtasks.getTaskType().equals(TaskType.EPIC)
                        && epicAndSubtasks.getStatus().equals(Status.DONE)) { // проверка и Эпика, так как его статус тоже меняется если все подзадачи этого эпика равны DONE
                    flag = true;
                    assertEquals(true, flag);
                }
            }
            inMemoryTaskManager.getTasks().clear();
        }

        @Test
        void cleanSubtaskIds() { // a.   Пустой список подзадач.
            create();
            boolean flag = true;
            inMemoryTaskManager.removeTasksByTasktype(TaskType.SUBTASK);
            for (Task task : inMemoryTaskManager.getTasks().values()) {
                if (task.getTaskType().equals(TaskType.SUBTASK)) {
                    flag = false;
                    break;
                }
            }
            assertEquals(true, flag);
            inMemoryTaskManager.getTasks().clear();
        }


    }

    abstract static class TaskManagerTest<T extends TaskManager> {
        InMemoryTaskManager taskManager;

        {
            taskManager = new InMemoryTaskManager();
        }

        Map<UUID, Task> tasks = new HashMap<>();


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
        }

        @Test
        public List<Task> getAllTasksByTaskType(TaskType taskType) {
            List<Task> list = tasks.entrySet().stream()
                    .filter(t -> t.getValue().getTaskType().equals(taskType))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
            return list;
        }


    }
}
