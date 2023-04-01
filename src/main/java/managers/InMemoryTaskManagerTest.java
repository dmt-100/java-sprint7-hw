package main.java.managers;

import main.java.intefaces.TaskManager;
import main.java.service.Status;
import main.java.service.TaskType;
import main.java.tasks.Epic;
import main.java.tasks.Subtask;
import main.java.tasks.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest<T extends TaskManager> extends TaskManagerTest {
    static String sep = File.separator;
    static final String savesTasksFile = String.join(sep, "src", "main", "java", "saves", "taskSaves" + ".csv");
    static File file = new File(savesTasksFile);

    static InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    Task task1 = new Task(TaskType.TASK, "Переезд", "Собрать коробки", Status.NEW);
    static UUID uuidTask;
    static UUID incorrectUuidTask = UUID.randomUUID();

    @Test
    static void create() {
        List<UUID> subtasksList = new ArrayList<>();
        Task task1 = new Task(UUID.randomUUID(), TaskType.TASK, "Переезд", "Собрать коробки", Status.NEW);
        inMemoryTaskManager.addNewTask(task1);
        Epic epic1 = new Epic(UUID.randomUUID(), TaskType.EPIC, "Переезд", "Переезд", Status.NEW, subtasksList);
        inMemoryTaskManager.addNewTask(epic1);
        Subtask subtask1 = new Subtask(UUID.randomUUID(), TaskType.SUBTASK, "тест1", "Собрать коробки", Status.NEW, epic1.getId(), LocalDateTime.now(), 50);
        inMemoryTaskManager.addNewTask(subtask1);
        Subtask subtask2 = new Subtask(UUID.randomUUID(), TaskType.SUBTASK, "тест2", "Упаковать кошку", Status.NEW, epic1.getId(), LocalDateTime.now(), 5);
        inMemoryTaskManager.addNewTask(subtask2);

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


    //---------------------------------------------------
    @Test
    void getTaskById() { // case 4
        create();
        // a. Со стандартным поведением.
        assertEquals(uuidTask, inMemoryTaskManager.getTask(uuidTask).getId());

        // b. С пустым списком задач.
        inMemoryTaskManager.getTasks().clear();
        NullPointerException ex = assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                inMemoryTaskManager.getTask(uuidTask).getId();
            }
        });
        assertNull(ex.getMessage());

        // c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
        create();
        NullPointerException ex2 = assertThrows(NullPointerException.class, () -> {
            inMemoryTaskManager.getTask(incorrectUuidTask).getId(); // несуществующий UUID
        });
        assertNull(ex2.getMessage());
        inMemoryTaskManager.getTasks().clear();
    }

    @Test
    void updateTask() {
        create();
        inMemoryTaskManager.getTask(uuidTask).getId();


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


//    @Override
//    public UUID addNewTask(Task task) {
//        return null;
//    }
//
//    @Override
//    public void removeTasksByTasktype(TaskType taskType) {
//
//    }
//
//    @Override
//    public Task getTask(UUID taskId) {
//        return null;
//    }
//
//    @Override
//    public void updateTask(Task task) {
//
//    }
//
//    @Override
//    public void removeTaskById(UUID id) {
//
//    }
//
//    @Override
//    public void changeStatusTask(UUID id, Status status) {
//
//    }
//
//    @Override
//    public List<Task> getSubtaskList(UUID epicId) {
//        return null;
//    }
//
//    @Override
//    public void updateEpicStatus(UUID epicId) {
//
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


}
