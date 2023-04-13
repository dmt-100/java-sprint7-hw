package main.java.tests;

import main.java.managers.FileBackedTasksManager;
import main.java.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
/*
4. Дополнительно для FileBackedTasksManager — проверка работы по сохранению и восстановлению состояния. Граничные условия:
     a. Пустой список задач.
     b. Эпик без подзадач.
     c. Пустой список истории.
*/

class FileBackedTasksManagerTest extends TaskManagerTest {

    private static final String sep = File.separator;
    private static final String saveTasksFilePath = String.join(sep, "src", "main", "java", "resources", "taskSaves" + ".csv");
    private static final File file = new File(saveTasksFilePath);

    FileBackedTasksManager fManager = new FileBackedTasksManager(file);

    @Override
    @BeforeEach
    void setTaskManager() {
        fManager = fileBackedTasksManager; // не могу понять зачем это нужно если все есть в таскменеджере, или чтото не до конца понимаю про наследование
    }
/*
Для каждого метода нужно проверить его работу:
  a. Со стандартным поведением.
  b. С пустым списком задач.
  c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
 */

    // TEST public List<Task> getHistoryTasks() ==========================================================
// единственный публичный метод в FileBackedTasksManager
    @Test
    void testGetHistoryTasksAndCheckWithMapOnMatchingWithStandardCondition() { // a. Со стандартным поведением.
        boolean flag = false;
        get();
        List<Task> tasks;
        tasks = fManager.getHistoryTasks();
        List<Task> tasksTest = new ArrayList<>();
        for (Task task : tasks) {
            if (fileBackedTasksManager.getTasks().containsKey(task.getId())) {
                flag = true;
            }
        }
        assertTrue(flag);
    }

    @Test
    void testGetHistoryTasksAndCheckWithMapOnMatchingWhenEmptyMap() { // a. Со стандартным поведением.
        clearHistory();
        boolean flag = false;
        get();
        List<Task> tasks;
        tasks = fManager.getHistoryTasks();
        assertEquals(new ArrayList<>(), tasks);
    }
 }