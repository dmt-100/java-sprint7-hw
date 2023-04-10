package main.java.intefaces;

import main.java.tasks.Task;

import java.util.List;
import java.util.UUID;

public interface HistoryManager {

    void add(Task task);

    String remove(UUID id);

    List<Task> getCustomLinkedList();

}
