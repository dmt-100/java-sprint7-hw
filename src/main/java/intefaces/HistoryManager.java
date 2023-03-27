package main.java.intefaces;

import main.java.tasks.Task;

import java.util.List;
import java.util.UUID;

public interface HistoryManager {

    void add(Task task);

    void remove(UUID id);

    List<Task> getCustomLinkedList();

}
