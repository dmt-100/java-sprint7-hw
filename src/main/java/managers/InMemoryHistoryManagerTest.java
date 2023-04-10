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

// Не совсем понял про Дублирование чего?? насколько я помню из ТЗ задача переписывается с новым временем
/*
    3. Для HistoryManager — тесты для всех методов интерфейса. Граничные условия:
     a. Пустая история задач.
     b. Дублирование.
     с. Удаление из истории: начало, середина, конец.
 */
class InMemoryHistoryManagerTest {
//    private final PrintStream standardOut = System.out;
//    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
//
//    @BeforeEach
//    public void setUp() {
//        System.setOut(new PrintStream(outputStreamCaptor));
//    }

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
        inMemoryHistoryManager.add(task1);

        epic1 = new Epic(
                TaskType.EPIC,
                "Переезд",
                "Переезд",
                Status.NEW,
                dateTimeTestEpic1,
                subtasksList);

        inMemoryHistoryManager.add(epic1);

        subtask1 = new Subtask(TaskType.SUBTASK, "тест1",
                "Собрать коробки", Status.NEW, LocalDateTime.now(), 50, epic1.getId());
        inMemoryHistoryManager.add(subtask1);

        subtasksList.add(subtask1.getId());

        uuidTask = task1.getId();
    }

    @Test
    void add() {
        create();
        assertEquals(task1, inMemoryHistoryManager.getCustomLinkedList().get(0));
    }

    @Test
    void getCustomLinkedList() {
        create();
        assertEquals(3, inMemoryHistoryManager.getCustomLinkedList().size());

    }

    @Test
    void remove() {
        inMemoryHistoryManager.remove(randomUuid);
        assertEquals("Задачи с таким id в истории нет", inMemoryHistoryManager.remove(randomUuid));

        create();
        assertEquals(3, inMemoryHistoryManager.getCustomLinkedList().size());
        inMemoryHistoryManager.remove(task1.getId());
        assertEquals(2, inMemoryHistoryManager.getCustomLinkedList().size());
        inMemoryHistoryManager.remove(epic1.getId());
        assertEquals(1, inMemoryHistoryManager.getCustomLinkedList().size());
        inMemoryHistoryManager.remove(subtask1.getId());
        assertEquals(0, inMemoryHistoryManager.getCustomLinkedList().size());
    }
}