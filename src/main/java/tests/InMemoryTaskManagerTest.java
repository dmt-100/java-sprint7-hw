package main.java.tests;

import main.java.managers.InMemoryTaskManager;
import main.java.service.Status;
import main.java.service.TaskType;
import main.java.tasks.Epic;
import main.java.tasks.Subtask;
import main.java.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryTaskManagerTest extends TaskManagerTest {
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    static String sep = File.separator;
    static String savesTasksFile = String.join(sep, "src", "main", "java", "saves", "taskSaves" + ".csv");
    static File file = new File(savesTasksFile);

    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
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

//    @Override
//    void setTaskManager() {
//
//    }

    @Test
    void create() {

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
                LocalDateTime.parse("2000-01-01T00:00:00"),
                50);
        inMemoryTaskManager.addNewTask(task1);

        epic1 = new Epic(
                TaskType.EPIC,
                "Переезд",
                "Переезд",
                Status.NEW,
                LocalDateTime.parse("2000-01-01T00:00:00"),
                subtasksList);
        inMemoryTaskManager.addNewTask(epic1);

        subtask1 = new Subtask(
                TaskType.SUBTASK,
                "тест1",
                "Собрать коробки",
                Status.NEW,
                LocalDateTime.parse("2000-01-01T00:00:00"),
                50,
                epic1.getId()
                );
        inMemoryTaskManager.addNewTask(subtask1);

        subtasksList.add(subtask1.getId());

        uuidTask = task1.getId();
    }
    /*
    Потребуются следующие тесты.
    1. Для расчёта статуса Epic. Граничные условия:
       a.   Пустой список подзадач.
       b.   Все подзадачи со статусом NEW.
       c.    Все подзадачи со статусом DONE.
       d.    Подзадачи со статусами NEW и DONE.
       e.    Подзадачи со статусом IN_PROGRESS.
    2. Для двух менеджеров задач InMemoryTasksManager и FileBackedTasksManager.
    Чтобы избежать дублирования кода, необходим базовый класс с тестами на каждый метод из интерфейса abstract class TaskManagerTest<T extends TaskManager>.
    Для подзадач нужно дополнительно проверить наличие эпика, а для эпика — расчёт статуса.
    Для каждого метода нужно проверить его работу:
      a. Со стандартным поведением.
      b. С пустым списком задач.
      c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
    3. Для HistoryManager — тесты для всех методов интерфейса. Граничные условия:
     a. Пустая история задач.
     b. Дублирование.
     с. Удаление из истории: начало, середина, конец.
    4. Дополнительно для FileBackedTasksManager — проверка работы по сохранению и восстановлению состояния. Граничные условия:
     a. Пустой список задач.
     b. Эпик без подзадач.
     c. Пустой список истории.
     */

    @Test
    void addNewTask() {
        Task task1 = new Task(
                TaskType.TASK,
                "Переезд",
                "Собрать коробки",
                Status.NEW,
                LocalDateTime.parse("2000-01-01T00:00:00"),
                50);
        inMemoryTaskManager.addNewTask(task1);
        inMemoryTaskManager.getTasks().put(task1.getId(), task1);
        assertEquals(task1, inMemoryTaskManager.getTask(task1.getId()));

        inMemoryTaskManager.getTasks().clear();
        Epic epic1 = new Epic(
                TaskType.EPIC,
                "Переезд",
                "Переезд",
                Status.NEW,
                LocalDateTime.parse("2000-01-01T00:00:00"),
                subtasksList);
        inMemoryTaskManager.addNewTask(epic1);
        Subtask subtask1 = new Subtask(
                TaskType.SUBTASK,
                "тест1",
                "Собрать коробки",
                Status.NEW,
                LocalDateTime.parse("2000-01-01T00:00:00"),
                50,
                epic1.getId()
        );
        inMemoryTaskManager.addNewTask(subtask1);
        UUID uuidSubtask;
        uuidSubtask = inMemoryTaskManager.getSubtasksFromEpic(epic1.getId()).get(0).getId();
        assertEquals(uuidSubtask, epic1.getSubtasks().get(0)); // проверка id сабтаска с тем что лежит в листе подзадач эпика
    }



    @Test
    void getAllTasksByTaskType() {
        create();
        List<Task> epics;
        epics = inMemoryTaskManager.getAllTasksByTaskType(TaskType.EPIC);
        assertEquals(epics.get(0), inMemoryTaskManager.getTasks().get(epic1.getId()));

        inMemoryTaskManager.getTasks().clear();
        epics = inMemoryTaskManager.getAllTasksByTaskType(TaskType.EPIC);
        assertEquals(new ArrayList<>(), epics);

        inMemoryTaskManager.getTasks().clear();
    }

    @Test
    void getTask() {
//        UUID uuidTask = UUID.fromString("00000000-0000-0000-0000-000000000000");
        create();
        // a. Со стандартным поведением.
        assertEquals(uuidTask, inMemoryTaskManager.getTask(uuidTask).getId());

        // b. С пустым списком задач.
        inMemoryTaskManager.getTasks().clear();
        NullPointerException ex = assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                inMemoryTaskManager.getTask(uuidTask).getId(); // не доконца разобрался еще почему Cannot invoke "main.java.tasks.Task.getId()"
            }
        });
        assertNull(ex.getMessage());

        // c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
        create();
        NullPointerException ex2 = assertThrows(NullPointerException.class, () -> {
            inMemoryTaskManager.getTask(randomUuid).getId(); // несуществующий UUID
        });
        assertNull(ex2.getMessage());
        inMemoryTaskManager.getTasks().clear();
    }

    @Test
    void updateTask() {
        create();
        taskUpdate = inMemoryTaskManager.getTask(uuidTask);
        taskUpdate.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateTask(taskUpdate);
        assertEquals(taskUpdate, inMemoryTaskManager.getTask(taskUpdate.getId()));
        inMemoryTaskManager.getTasks().clear();
    }

    @Test
    void removeTaskById() {
        create();
        inMemoryTaskManager.removeTaskById(task1.getId());
        assertNull(inMemoryTaskManager.getTask(task1.getId()));

        inMemoryTaskManager.removeTaskById(subtask1.getId()); // по логике эпик удаляется только если у него нет сабтасков, но это нужно уточнить
        inMemoryTaskManager.removeTaskById(epic1.getId());
        assertNull(inMemoryTaskManager.getTask(epic1.getId()));
        assertNull(inMemoryTaskManager.getTask(subtask1.getId())); // проверка и на удаление подзадач

        create();

        NullPointerException ex = assertThrows(NullPointerException.class, () -> {
            inMemoryTaskManager.removeTaskById(randomUuid); // несуществующий UUID
        });
        assertTrue(ex.getMessage().contentEquals("Неверный идентификатор задачи"));

        inMemoryTaskManager.getTasks().clear();
    }

    @Test
    void changeStatusTask() {
        create();
        inMemoryTaskManager.changeStatusTask(task1.getId(), Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, inMemoryTaskManager.getTasks().get(task1.getId()).getStatus());

        inMemoryTaskManager.getTasks().clear();
        NullPointerException ex = assertThrows(NullPointerException.class, () -> {
            inMemoryTaskManager.changeStatusTask(task1.getId(), Status.IN_PROGRESS);
        });
        assertTrue(ex.getMessage().contentEquals("Неверный идентификатор задачи"));

        inMemoryTaskManager.getTasks().clear();
    }

    @Test
    void getSubtaskList() {
        create();
        List<Task> subtasks;
        subtasks = inMemoryTaskManager.getSubtasksFromEpic(epic1.getId());
        Task subtask = inMemoryTaskManager.getTasks().get(subtask1.getId());
        assertEquals(subtasks.get(0), subtask);

        inMemoryTaskManager.getTasks().clear();
        assertEquals(new ArrayList<>(), inMemoryTaskManager.getSubtasksFromEpic(epic1.getId()));

        create();
        NullPointerException ex = assertThrows(NullPointerException.class, () -> {
            inMemoryTaskManager.getSubtasksFromEpic(randomUuid);
        });
        assertTrue(ex.getMessage().contentEquals("Неверный идентификатор задачи"));


        inMemoryTaskManager.getTasks().clear();
    }

    // ============================================================================
    // Блок тестов на изменения статусов
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
        assertEquals(1, statusDoneOrNew);
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
        assertEquals(1, statusInprogress);
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
                assertTrue(flag);
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
        assertTrue(flag);
        inMemoryTaskManager.getTasks().clear();
    }

    // ============================================================================

    @Test
    void prioritizeTasks() {
        create();
        inMemoryTaskManager.prioritizeTasks();
        LocalDateTime testTime = LocalDateTime.parse("1999-01-01 00:00:00",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)); // 2014-12-22T05:10:30
        boolean flag = false;
        for (Task task : inMemoryTaskManager.getTasks().values()) {
            if (task.getStartTime().isAfter(testTime)) {
                flag = true;
                testTime = task.getStartTime();
            }
        }
        assertTrue(flag);
    }
}