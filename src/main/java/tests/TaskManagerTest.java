package main.java.tests;

import main.java.intefaces.TaskManager;
import main.java.managers.FileBackedTasksManager;
import main.java.service.Status;
import main.java.service.TaskType;
import main.java.tasks.Epic;
import main.java.tasks.Subtask;
import main.java.tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    abstract void setTaskManager();

    private static final String sep = File.separator;
    private static final String saveTasksFilePath = String.join(sep, "src", "main", "java", "resources", "taskSaves" + ".csv");
    private static File file = new File(saveTasksFilePath);
    FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);

    public void FileBackedTasksManagerTest(File file) {
        FileBackedTasksManager.file = file;
    }

    List<UUID> subtasks = new ArrayList<>();

    UUID epicUuid = UUID.fromString("11111111-d496-48c2-bb4a-f4cf88f18e23");
    UUID testUuid = UUID.fromString("00000000-0000-48c2-bb4a-f4cf88f18e23");
    UUID wrongUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");

    Task task = new Task(
            TaskType.TASK,
            "Переезд",
            "Собрать коробки",
            Status.NEW,
            LocalDateTime.parse("2000-01-01T00:00:00"),
            50);

    Task epic = new Epic(
            epicUuid,
            TaskType.EPIC,
            "Переезд",
            "Переезд",
            Status.NEW,
            LocalDateTime.parse("2000-01-01T02:00:00"),
            0,
            subtasks);


    Task subtask = new Subtask(
            TaskType.SUBTASK,
            "тест1",
            "Собрать коробки",
            Status.NEW,
            LocalDateTime.parse("2000-01-01T04:00:00"),
            50,
            epicUuid);


    Task taskUpdate;
    static UUID uuidTask;
    static UUID randomUuid = UUID.randomUUID();


    // 1) все в порядке, задача достается,
    @BeforeEach
    void createFirst() {
        fileBackedTasksManager.addNewTask(task);
        fileBackedTasksManager.addNewTask(epic);
        fileBackedTasksManager.addNewTask(subtask);
        epic.setSubtasks(subtask.getId());
    }

    // 1) все в порядке, задача достается,

    void get() {
        fileBackedTasksManager.getTask(task.getId());
        fileBackedTasksManager.getTask(epic.getId());
        fileBackedTasksManager.getTask(subtask.getId());
    }

    // 1) все в порядке, задача достается,
    void createThird() {
        fileBackedTasksManager.addNewTask(task);
        fileBackedTasksManager.addNewTask(epic);
//        fileBackedTasksManager.addNewTask(subtask);
    }

    @AfterEach
    void clearHistory() {
        if (fileBackedTasksManager.getTasks().containsKey(task.getId())) {
            fileBackedTasksManager.removeTaskById(task.getId());
        }
        if (fileBackedTasksManager.getTasks().containsKey(subtask.getId())) {
            fileBackedTasksManager.removeTaskById(subtask.getId());
        }
        if (fileBackedTasksManager.getTasks().containsKey(epic.getId())) {
            fileBackedTasksManager.removeTaskById(epic.getId());
        }
        fileBackedTasksManager.getTasks().clear();
    }


    // =================================== /case 1/ void addNewTask(Task task) ===================================
/*
  a. Со стандартным поведением. (из ТЗ)
  b. С пустым списком задач.
  c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
 */
    @Test
    void testCase1AddNewTask1() { // a. Со стандартным поведением. (из ТЗ)
//        fileBackedTasksManager.addNewTask(task);
        assertEquals(task, fileBackedTasksManager.getTasks().get(task.getId()));
    }

    @Test
    void testCase1AddNewTask2() { // b. С пустым списком задач.
        fileBackedTasksManager.getTasks().clear();
        Task taskActual = fileBackedTasksManager.getTask(task.getId());
        assertNull(taskActual);
    }

    @Test
    void testCase1AddNewTask3() { // c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
        Subtask taskActual = (Subtask) fileBackedTasksManager.getTask(wrongUuid);
        assertNull(taskActual);
    }
// ====================== /case 2/ List<Task> getAllTasksByTaskType(TaskType taskType) ======================

    @Test
    void testCase2GetAllTasksByTaskTypeWithStandartCondition() {   // a. Со стандартным поведением. (из ТЗ)
        List<Task> epics;
        epics = fileBackedTasksManager.getAllTasksByTaskType(TaskType.EPIC); // return List<Task>
        assertEquals(epics.get(0), fileBackedTasksManager.getTasks().get(epic.getId()));

        fileBackedTasksManager.getTasks().clear();
        epics = fileBackedTasksManager.getAllTasksByTaskType(TaskType.EPIC);

        assertEquals(new ArrayList<>(), epics);
    }

    @Test
    void testCase2GetAllTasksByTaskTypeFromEmptyMap() {  // b. С пустым списком задач.
        clearHistory();
        List<Task> epics;
        epics = fileBackedTasksManager.getAllTasksByTaskType(TaskType.EPIC);

        assertEquals(new ArrayList<>(), epics);
    }

    // ============================= /case 3/ void removeTasksByTasktype(TaskType taskType) =============================
    @Test
    void testCase3RemoveTasksByTasktypeWithStandartCondition() {  // a. Со стандартным поведением. (из ТЗ)
        assertEquals(TaskType.EPIC, fileBackedTasksManager.getTasks().get(epic.getId()).getTaskType());

    }

    @Test
    void testCase3RemoveTasksByTasktypeFromEmptyMap() {  // b. С пустым списком задач.
        clearHistory();
        Task taskActual = fileBackedTasksManager.getTask(task.getId());

        assertNull(taskActual);
    }

// ===================================== /case 4/ Task getTask(UUID taskId) =====================================

    // case 4
    @Test
    void testCase4GetTaskWithStandardCondition() { // 1) все в порядке, задача достается,
        Task taskActual = fileBackedTasksManager.getTask(task.getId());
        assertEquals(task, taskActual);

    }

    @Test
    void testCase4getTaskFromEmptyMap() { // 2) список пуст, соответветственно задача не достанется,
        clearHistory();
        Task taskActual = fileBackedTasksManager.getTask(task.getId());
        assertNull(taskActual);
    }

    @Test
    void testCase4getTaskWithoutOurTaskInMap() { // 3) в списке что-то есть, но нет нашей задачи, задача не достанется
        Task taskTest = new Task(
                testUuid,
                TaskType.TASK,
                "Переезд",
                "Собрать коробки",
                Status.NEW,
                LocalDateTime.parse("2000-01-01T00:00:00"),
                50);
        Subtask taskActual = (Subtask) fileBackedTasksManager.getTask(taskTest.getId());
        assertNull(taskActual);
    }


// ===================================== /case 5/ void updateTask(Task task) =====================================

    @Test
    void testCase5UpdateTaskWithStandardCondition() { // a. Со стандартным поведением. (из ТЗ)
        UUID uuidForTestCase5 = task.getId();
        Task taskTest = new Task(
                uuidForTestCase5,
                TaskType.TASK,
                "Переезд TECT",
                "Собрать коробки",
                Status.NEW,
                LocalDateTime.parse("2000-01-01T00:00:00"),
                50);
        fileBackedTasksManager.updateTask(taskTest);
        Task taskActual = fileBackedTasksManager.getTask(taskTest.getId());
        assertEquals(taskTest, taskActual);
    }

    @Test
    void testCase5UpdateTaskWhenEmptyMap() { // b. С пустым списком задач.
        clearHistory();
        fileBackedTasksManager.updateTask(task);
        Map<UUID, Task> shouldBeEmpty = new HashMap<>(fileBackedTasksManager.getTasks());
        assertEquals(new HashMap<>(), shouldBeEmpty);
    }

    @Test
    void testCase5UpdateTaskWithoutOurTaskInMap() { // c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
        clearHistory();
        Task taskTest = new Task(
                testUuid,
                TaskType.TASK,
                "Переезд",
                "Собрать коробки",
                Status.NEW,
                LocalDateTime.parse("2000-01-01T00:00:00"),
                50);
        fileBackedTasksManager.updateTask(taskTest);
        Map<UUID, Task> shouldBeEmpty = new HashMap<>(fileBackedTasksManager.getTasks());
        assertEquals(new HashMap<>(), shouldBeEmpty);
    }

// ===================================== /case 6/ void removeTaskById(UUID id) =====================================

    @Test
    void testCase6RemoveTaskByIdWithStandardCondition() { // a. Со стандартным поведением. (из ТЗ)
        fileBackedTasksManager.removeTaskById(task.getId());
        assertNull(fileBackedTasksManager.getTasks().get(task.getId()));
    }

    @Test
    void testCase6RemoveTaskByIdWhenEmptyMap() { // b. С пустым списком задач.
        clearHistory();
        assertNull(fileBackedTasksManager.getTasks().get(task.getId()));
    }

    @Test
    void testCase6RemoveTaskByIdWithoutOurTaskInMap() { // c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
        Task taskTest = new Task(
                testUuid,
                TaskType.TASK,
                "Переезд",
                "Собрать коробки",
                Status.NEW,
                LocalDateTime.parse("2000-01-01T00:00:00"),
                50);
        NullPointerException ex = assertThrows(NullPointerException.class, () -> {
            fileBackedTasksManager.removeTaskById(taskTest.getId());
        });
        assertTrue(ex.getMessage().contentEquals("Неверный идентификатор задачи"));
    }

// ============================ /case 7/ void changeStatusTask(UUID id, Status status) ============================

    @Test
    void testCase7changeStatusTaskWithStandardCondition() { // a. Со стандартным поведением. (из ТЗ)
        fileBackedTasksManager.changeStatusTask(task.getId(), Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, fileBackedTasksManager.getTasks().get(task.getId()).getStatus());
    }


    @Test
    void testCase7changeStatusTaskWhenEmptyMap() { // b. С пустым списком задач.
        clearHistory();

        assertNull(fileBackedTasksManager.getTasks().get(task.getId()));
        NullPointerException ex = assertThrows(NullPointerException.class, () -> {
            fileBackedTasksManager.changeStatusTask(task.getId(), Status.IN_PROGRESS);
        });
        assertTrue(ex.getMessage().contentEquals("Неверный идентификатор задачи"));
    }

    @Test
    void testCase7changeStatusTaskWithoutOurTaskInMap() { // c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
        Epic epic = new Epic(
                testUuid, // тестовый, то есть задача другая
                TaskType.EPIC,
                "Переезд",
                "Переезд",
                Status.NEW,
                LocalDateTime.parse("2000-01-01T00:00:00"),
                0,
                subtasks);
        NullPointerException ex = assertThrows(NullPointerException.class, () -> {
            fileBackedTasksManager.changeStatusTask(epic.getId(), Status.IN_PROGRESS);
        });
        assertTrue(ex.getMessage().contentEquals("Неверный идентификатор задачи"));
    }

// ============================ /case 8/ List<Task> getSubtasksFromEpic(UUID epicId) ============================

    @Test
    void testCase8GetSubtasksFromEpicWithStandardCondition() { // a. Со стандартным поведением. (из ТЗ)
        List<Task> subtasks = new ArrayList<>(fileBackedTasksManager.getSubtasksFromEpic(epic.getId()));
        boolean flag = fileBackedTasksManager.getTask(subtask.getId()).equals(subtasks.get(1)); // subtasks.size = 2! не могу понять почему Not showing null elements при значении 0  https://i.ibb.co/phMhZYw/image.png  возможно связано с хешкодом  https://stackoverflow.com/questions/59150344/arraylist-for-loop-in-to-a-hashmap-size-shows-is-correct-but-elements-are-one-le
        assertTrue(flag);
    }

    @Test
    void testCase8GetSubtasksFromEpicWhenEmptyMap() { // b. С пустым списком задач.
        clearHistory();

        assertEquals(new ArrayList<>(), fileBackedTasksManager.getSubtasksFromEpic(randomUuid)); // "Мапа пуста"
    }

    @Test
    void testCase8GetSubtasksFromEpicWithWrongId() { // c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
        NullPointerException ex = assertThrows(NullPointerException.class, () -> {
            fileBackedTasksManager.getSubtasksFromEpic(randomUuid);
        });
        assertTrue(ex.getMessage().contentEquals("Неверный идентификатор задачи"));
    }

// =================================== /case 9/ List<Task> getHistory() ===================================

    @Test
    void testCase9GetHistoryWithStandardCondition() { // a. Со стандартным поведением. (из ТЗ)
        fileBackedTasksManager.getTask(epic.getId()); // заполняем историю по порядку
        fileBackedTasksManager.getTask(task.getId());
        List<Task> tasksByHistory = fileBackedTasksManager.getHistory();
        boolean flag = (tasksByHistory.get(0).getTaskType().equals(TaskType.EPIC) &&
                tasksByHistory.get(1).getTaskType().equals(TaskType.TASK));
        assertTrue(flag);
    }

    @Test
    void testCase9GetHistoryWhenEmptyMap() { // b. С пустой мапой задач.

        List<Task> tasksByHistory = fileBackedTasksManager.getHistory();
        assertEquals(new ArrayList<>(), tasksByHistory);
    }

// =================================== /case 11/ Set<Task> getPrioritizedTasks() ===================================

    @Test
    void testCase11GetPrioritizedTasksWithStandardCondition() { // a. Со стандартным поведением. (из ТЗ)
        fileBackedTasksManager.prioritizeTasks();
        boolean flag;
        List<Task> tasks = new ArrayList<>(fileBackedTasksManager.getPrioritizedTasks());
        flag = tasks.get(0).getTaskType().equals(TaskType.TASK) &&
                tasks.get(1).getTaskType().equals(TaskType.EPIC) &&
                tasks.get(2).getTaskType().equals(TaskType.SUBTASK);
        assertTrue(flag);
    }

    @Test
    void testCase11GetPrioritizedTasksWhenEmptyMap() {  // b. С пустой мапой задач.
        clearHistory();
        fileBackedTasksManager.prioritizeTasks();
        boolean flag;
        List<Task> tasks = new ArrayList<>(fileBackedTasksManager.getPrioritizedTasks());

        assertEquals(new ArrayList<>(), tasks);
    }
}

/*
  a. Со стандартным поведением. (из ТЗ)
  b. С пустым списком задач.
  c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).

например, если тестировать метод getTask(Task task),
то должно быть 3 теста на этот метод,
1) все в порядке, задача достается,
2) список пуст, соответветственно задача не достанется,
3) в списке что-то есть, но нет нашей задачи, задача не достанется
и так по каждому методу TaskManager
 */

/*
     1.    void addNewTask(Task task);
     2.    List<Task> getAllTasksByTaskType(TaskType taskType);
     3.    void removeTasksByTasktype(TaskType taskType);
     4.    Task getTask(UUID taskId);
     5.    void updateTask(Task task);
     6.    void removeTaskById(UUID id);
     7.    void changeStatusTask(UUID id, Status status);
     8.    List<Task> getSubtasksFromEpic(UUID epicId);
     9.    void updateEpicStatus(UUID epicId);
     10.    List<Task> getHistory();
     11.    Map<UUID, Task> getTasks();
     12.    Set<Task> getPrioritizedTasks();
*/

/*
  a. Со стандартным поведением. (из ТЗ)
  b. С пустым списком задач.
  c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
 */
    //    public List<Task> getAllTasksByTaskType(TaskType taskType) {
//        List<Task> list = tasks.entrySet().stream()
//                .filter(t -> t.getValue().getTaskType().equals(taskType))
//                .map(Map.Entry::getValue)
//                .collect(Collectors.toList());
//        return list;
//    }
/*
Для каждого метода нужно проверить его работу:
  a. Со стандартным поведением.
  b. С пустым списком задач.
  c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
 */
//    @Test
//    void getTaskById() {
//    }
//
//    @Test
//    void updateTask() {
//    }
//
//    @Test
//    void removeTaskById() {
//    }
//
//    @Test
//    void changeStatusTask() {
//    }
//
//    @Test
//    void getSubtaskList() {
//    }
//
//    @Test
//    void updateEpicStatus() {
//    }
//
//    @Test
//    void getHistory() {
//    }
//
//    @Test
//    void getTasks() {
//
//    }

/*
    void addNewTask(Task task);

    // case 2: Получение списка всех задач.-------------------------------------
    List<Task> getAllTasksByTaskType(TaskType taskType);

    void removeTasksByTasktype(TaskType taskType);

    Task getTask(UUID taskId);

    void updateTask(Task task);

    void removeTaskById(UUID id);

    void changeStatusTask(UUID id, Status status);


    List<Task> getSubtasksFromEpic(UUID epicId);

    void updateEpicStatus(UUID epicId);

    List<Task> getHistory();  // history

    Set<Task> getPrioritizedTasks();  // case 11
    
    Map<UUID, Task> getTasks(); // геттер

 */