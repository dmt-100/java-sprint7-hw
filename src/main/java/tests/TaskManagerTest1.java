//package main.java.tests;
//
//import main.java.managers.FileBackedTasksManager;
//import main.java.service.Status;
//import main.java.service.TaskType;
//import main.java.tasks.Epic;
//import main.java.tasks.Subtask;
//import main.java.tasks.Task;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.File;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class TaskManagerTest1 {
//
//    private static final String sep = File.separator;
//    private static final String saveTasksFilePath = String.join(sep, "src", "main", "java", "resources", "taskSaves" + ".csv");
//    private static File file = new File(saveTasksFilePath);
//
//    FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
//
//    List<UUID> subtasks = new ArrayList<>();
//    Task task1;
//    Epic epic1;
//    Subtask subtask1;
//
//    Task taskUpdate;
//    static UUID uuidTask;
//    static UUID randomUuid = UUID.randomUUID();
//
//    LocalDateTime dateTimeTestTask1;
//    LocalDateTime dateTimeTestTask2;
//    LocalDateTime dateTimeTestEpic1;
//    LocalDateTime dateTimeTestSubtask1;
//
//    @BeforeEach
//    void create() { // по три задачи
//
//        dateTimeTestTask1 = LocalDateTime.parse("2014-12-22T05:10:30");
//        dateTimeTestTask2 = LocalDateTime.parse("2014-12-22T05:00:30");
//        dateTimeTestEpic1 = LocalDateTime.parse("2015-12-22T08:15:30");
//        dateTimeTestSubtask1 = LocalDateTime.parse("2016-12-22T10:20:30");
//
//        List<UUID> subtasksList = new ArrayList<>();
//
//        task1 = new Task(
//                TaskType.TASK,
//                "Переезд",
//                "Собрать коробки",
//                Status.NEW,
//                dateTimeTestTask1,
//                50);
//
//        epic1 = new Epic(
//                TaskType.EPIC,
//                "Переезд",
//                "Переезд",
//                Status.NEW,
//                dateTimeTestEpic1,
//                subtasksList);
//
//
//        subtask1 = new Subtask(
//                TaskType.SUBTASK,
//                "тест1",
//                "Собрать коробки",
//                Status.NEW,
//                dateTimeTestSubtask1,
//                50,
//                epic1.getId());
//
//    }
//
//    @AfterEach
//    void clearHistory() {
//        if (fileBackedTasksManager.getTasks().containsKey(task1.getId())) {
//            fileBackedTasksManager.removeTaskById(task1.getId());
//        }
//        if (fileBackedTasksManager.getTasks().containsKey(subtask1.getId())) {
//            fileBackedTasksManager.removeTaskById(subtask1.getId());
//        }
//        if (fileBackedTasksManager.getTasks().containsKey(epic1.getId())) {
//            fileBackedTasksManager.removeTaskById(epic1.getId());
//        }
//    }
///*
//например, если тестировать метод getTask(Task task),
//то должно быть 3 теста на этот метод,
//1) все в порядке, задача достается,
//2) список пуст, соответветственно задача не достанется,
//3) в списке что-то есть, но нет нашей задачи, задача не достанется
//и так по каждому методу TaskManager
// */
//
///*
//1.    void addNewTask(Task task);
//2.    List<Task> getAllTasksByTaskType(TaskType taskType);
//3.    void removeTasksByTasktype(TaskType taskType);
//4.    Task getTask(UUID taskId);
//5.    void updateTask(Task task);
//6.    void removeTaskById(UUID id);
//7.    void changeStatusTask(UUID id, Status status);
//8.    List<Task> getSubtasksFromEpic(UUID epicId);
//9.    void updateEpicStatus(UUID epicId);
//10.    List<Task> getHistory();
//11.    Map<UUID, Task> getTasks();
//12.    Set<Task> getPrioritizedTasks();
// */
//    @Test // 1.    void addNewTask(Task task);
//    void addNewTask() {
//        fileBackedTasksManager.addNewTask(task1);
//        assertEquals(task1, fileBackedTasksManager.getTask(task1.getId()));
//    }
//
//    @Test
//    void getAllTasksByTaskType() {
//        fileBackedTasksManager.addNewTask(task1);
//        fileBackedTasksManager.addNewTask(epic1);
//        fileBackedTasksManager.addNewTask(subtask1);
//    }
//
///*
//1.    void addNewTask(Task task);
//2.    List<Task> getAllTasksByTaskType(TaskType taskType);
//3.    void removeTasksByTasktype(TaskType taskType);
//4.    Task getTask(UUID taskId);
//5.    void updateTask(Task task);
//6.    void removeTaskById(UUID id);
//7.    void changeStatusTask(UUID id, Status status);
//8.    List<Task> getSubtasksFromEpic(UUID epicId);
//9.    void updateEpicStatus(UUID epicId);
//10.    List<Task> getHistory();
//11.    Map<UUID, Task> getTasks();
//12.    Set<Task> getPrioritizedTasks();
// */
//}
