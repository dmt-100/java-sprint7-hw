package main.java.managers;

import main.java.intefaces.HistoryManager;
import main.java.service.Node;
import main.java.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
     private final CustomLinkedList<Task> customLinkedList = new CustomLinkedList<>(); // класс с нодами

    @Override
    public void add(Task task) {        // case 4, case 8
        if (task != null) {
            customLinkedList.linkLast(task);
            customLinkedList.removeNode(customLinkedList.customLinkedNodes.get(task.getId()));
            customLinkedList.customLinkedNodes.put(task.getId(), customLinkedList.tail);
        }
    }

    @Override
    public ArrayList<Task> getTasksInHistory() {
        return customLinkedList.getTasksByNodes();
    }

    @Override
    public Map<UUID, Node<Task>> getUuidNodes() {
        return customLinkedList.getCustomLinkedNodes();
    }

    @Override
    public String remove(UUID id) {
        String str;
        if (!customLinkedList.customLinkedNodes.containsKey(id)) {
            str = "Задачи с таким id в истории нет";
        } else {
            customLinkedList.removeNode(customLinkedList.customLinkedNodes.get(id));    // вначале удаляет ноду
            customLinkedList.customLinkedNodes.remove(id);                              // затем перезаписывает
            str = "Задача удалена из истории";
        }
        return str;
    }



}

class CustomLinkedList<Task> {
    protected Map<UUID, Node<Task>> customLinkedNodes = new HashMap<>();
    private Node<Task> head;
    protected Node<Task> tail;
    private Node<Task> temp; // для повторного использования getCustomLinkedList()

    public void linkLast(Task task) {
//        if (tail != null) { // гдето на тестах пришлось убрать, вроде как не нужна, удалить потом
            final Node<Task> oldTail = tail;
            final Node<Task> newNode = new Node<>(oldTail, task, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
                temp = head;
            } else {
                oldTail.next = newNode;
            }
//        }
    }

    public ArrayList<Task> getTasksByNodes() {
        final ArrayList<Task> tasks = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            tasks.add(current.get());
            head = head.next;
            current = head;
        }
        head = temp;
        return tasks;
    }

    public void removeNode(Node node) {
        if (node != null) {
            final Node<Task> next = node.next;
            final Node<Task> prev = node.prev;

            // Если в списке всего один элемент и мы его удаляем, то хвост и голова должны стать null
            if (next == null && prev == null) {
                head = null;
                tail = null;
            } else {
                if (prev == null) {
                    head = next;
                } else {
                    prev.next = next;
                    node.prev = null;
                }
                if (next == null) {
                    tail = prev;
                } else {
                    next.prev = prev;
                    node.next = null;
                }
            }
        }
    }

    public Map<UUID, Node<Task>> getCustomLinkedNodes() {
        return customLinkedNodes;
    }
}
//    public void removeNode(Node<Task> node) { // косячный убил полдня на поиск ошибки *head и tail нули
//        if (node.next == null && node.prev == null) {
//            head = null;
//            tail = null;
//        } else {
//            if (node.prev == null) {
//                head = node.next;
//            } else {
//                node.prev.next = node.next;
//                node.prev = null;
//            }
//            if (node.next == null) {
//                tail = node.prev;
//            } else {
//                node.next.prev = node.prev;
//                node.next = null;
//            }
//        }
//    }
