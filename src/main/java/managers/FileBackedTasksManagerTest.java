package main.java.managers;

import main.java.service.Status;
import main.java.service.TaskType;
import main.java.tasks.Epic;
import main.java.tasks.Subtask;
import main.java.tasks.Task;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
/*
4. Дополнительно для FileBackedTasksManager — проверка работы по сохранению и восстановлению состояния. Граничные условия:
     a. Пустой список задач.
     b. Эпик без подзадач.
     c. Пустой список истории.
*/

class FileBackedTasksManagerTest {

    private static final String sep = File.separator;
    private static final String saveTasksFilePath = String.join(sep, "src", "main", "java", "resources", "taskSaves" + ".csv");
    private static final File file = new File(saveTasksFilePath);

    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
    InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    List<UUID> subtasksList = new ArrayList<>();
    Task task1;
    Epic epic1;
    Subtask subtask1;
    Task taskUpdate;
    static UUID uuidTask;
    static UUID randomUuid = UUID.randomUUID();

    LocalDateTime dateTimeTestTask1;
    LocalDateTime dateTimeTestTask2;
    LocalDateTime dateTimeTestEpic1;
    LocalDateTime dateTimeTestSubtask1;

    @Test
    void create() { // по три задачи

        dateTimeTestTask1 = LocalDateTime.parse("2014-12-22T05:10:30");
        dateTimeTestTask2 = LocalDateTime.parse("2014-12-22T05:00:30");
        dateTimeTestEpic1 = LocalDateTime.parse("2015-12-22T08:15:30");
        dateTimeTestSubtask1 = LocalDateTime.parse("2016-12-22T10:20:30");

        List<UUID> subtasksList = new ArrayList<>();

        task1 = new Task(
                TaskType.TASK,
                "Переезд",
                "Собрать коробки",
                Status.NEW,
                dateTimeTestTask1,
                50);
        fileBackedTasksManager.addNewTask(task1);

        epic1 = new Epic(
                TaskType.EPIC,
                "Переезд",
                "Переезд",
                Status.NEW,
                dateTimeTestEpic1,
                subtasksList);

        fileBackedTasksManager.addNewTask(epic1);

        subtask1 = new Subtask(
                TaskType.SUBTASK,
                "тест1",
                "Собрать коробки",
                Status.NEW,
                dateTimeTestSubtask1,
                50,
                epic1.getId());
        fileBackedTasksManager.addNewTask(subtask1);

        subtasksList.add(subtask1.getId());

        // заполняем историю
        fileBackedTasksManager.getTask(task1.getId());
        fileBackedTasksManager.getTask(epic1.getId());
        fileBackedTasksManager.getTask(subtask1.getId());
    }
    @Test
    void clearHistory() {
        fileBackedTasksManager.removeTaskById(task1.getId());
        fileBackedTasksManager.removeTaskById(epic1.getId());
        fileBackedTasksManager.removeTaskById(subtask1.getId());
    }


    @Test
    void getHistoryTasks() { // проверка на запись задач и чтение
        create();
        boolean flag = false;
//        List<UUID> uuids = new ArrayList<>(fileBackedTasksManager.getTask().getId());
        List<UUID> uuids = new ArrayList<>();
        uuids.add(task1.getId());
        uuids.add(epic1.getId());
        uuids.add(subtask1.getId());
        for (UUID uuid : uuids) {
            fileBackedTasksManager.getTask(uuid); // заполнение просмотра истории (case 4)
        }

        List<UUID> uuidsInHistory = new ArrayList<>();
        for (Task historyTask : fileBackedTasksManager.getHistoryTasks()) {
            uuidsInHistory.add(historyTask.getId());
        }

        for (int i = 0; i < uuids.size(); i++) {
            if (uuids.get(i).equals(uuidsInHistory.get(i))) {
                flag = true;
            }
        }
        assertTrue(flag); // true если все id задач добавлены в файл и прочтены

// =========================================================================================================

        for (UUID uuid : uuidsInHistory) {
            fileBackedTasksManager.removeTaskById(uuid); // очищаем историю задач
        }
        assertEquals(null, fileBackedTasksManager.getTask(subtask1.getId())); // даже не знаю как лучше может лучше обернуть в try/catch на NullPointer вместо if/else в методе getTask

    }

    @Test
    void epicWithoutSubtusks() { // Эпик без подзадач

        create();
        fileBackedTasksManager.removeTasksByTasktype(TaskType.SUBTASK);
        boolean isSubtaskInHistory = false;
        if (!fileBackedTasksManager.getHistoryTasks().contains(subtask1)) {
            isSubtaskInHistory = true;
        }
        assertTrue(isSubtaskInHistory);
        clearHistory();
    }

    @Test
    void dublirovanie() { // case 5
        create();

        task1.setDescription("Сказать слова прощания test Case5");
        fileBackedTasksManager.updateTask(task1);
        String newDescripttion = "";
        for (Task task : fileBackedTasksManager.getHistoryManager().getCustomLinkedList()) {
           if (task.getId().equals(task1.getId())) {
               newDescripttion = task.getDescription();
           }
        }
        assertEquals(task1.getDescription(), newDescripttion);
    }
 }