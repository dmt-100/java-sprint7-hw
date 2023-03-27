package main.java.managers;

import main.java.intefaces.HistoryManager;
import main.java.intefaces.TaskManager;
import main.java.managers.InMemoryHistoryManager;
import main.java.managers.InMemoryTaskManager;

public class Managers {

    public static  HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static  TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}
