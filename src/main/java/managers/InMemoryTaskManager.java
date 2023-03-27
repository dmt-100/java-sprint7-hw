package main.java.managers;

import main.java.intefaces.HistoryManager;
import main.java.intefaces.TaskManager;
import main.java.service.Status;
import main.java.service.TaskType;
import main.java.tasks.Epic;
import main.java.tasks.Task;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<UUID, Task> tasks = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void addNewTask(Task task) {
        if (task.getTaskType().equals(TaskType.EPIC)) {
            tasks.put(task.getId(), task);
            System.out.println("Эпик успешно добавлен");
        } else { // временно из-за Назначения Id эпика
            task.setId(UUID.randomUUID()); //
            if (task.getTaskType().equals(TaskType.SUBTASK)) {
                Epic epic = (Epic) tasks.get(task.getEpicId()); // нашел решение только через кастинг
                epic.getSubtasks().add(task.getId());
                updateTask(epic);
            }
            tasks.put(task.getId(), task);
            System.out.println("Задача успешно добавлена");
        }

    }

    // case 2: Получение списка всех задач.-------------------------------------
    @Override
    public List<Task> getAllTasksByTaskType(TaskType taskType) {
        List<Task> list = tasks.entrySet().stream()
                .filter(t -> t.getValue().getTaskType().equals(taskType))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        return list;
    }


    // case 3: Удаление всех задач по типу.---------------------------------------
    @Override
    public void removeTasksByTasktype(TaskType taskType) {
        if (taskType.equals(TaskType.SUBTASK)) {
            tasks.values().stream()
                    .forEach(t -> t.getSubtasks().clear()); // удаление списка подзадач у Эпиков
        }
        tasks.entrySet().removeIf(entry -> taskType.equals(entry.getValue().getTaskType()));
    }

    // ТЗ-4
    // case 4:get методы-------------------------------------------------------------
    @Override
    public void getTaskById(UUID idInput) {
        Task task = null;
        if (tasks.containsKey(idInput)) {
            task = tasks.get(idInput);
            historyManager.add(task);
        }

    }

    // case 5: Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
// "Это значит что в объекте Task заполнено поле id и мы можем его использовать для обновления объекта. поэтому во всех трёх методах должен на вход подаваться только объект задачи"
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            System.out.println("Обновление задачи прошло успешно");
        }
    }

    // case 6: Удалить по идентификатору. ----------------------------------------
    @Override
    public void removeTaskById(UUID id) {
        tasks.keySet().removeIf(u -> u.equals(id)); // Predicate
        historyManager.remove(id);
        System.out.println("Задача удалена");

//        tasks.entrySet().stream()
//                .filter(t -> t.getKey().equals(id))
//                .filter(t -> t.getValue().getTaskType().equals(TaskType.SUBTASK))
//                .map(t -> t.getValue().getTaskType().equals(TaskType.SUBTASK))
//                .forEach(updateEpicStatus(id));
        if (tasks.containsKey(id)) {
            if (tasks.get(id).getTaskType().equals(TaskType.SUBTASK)) {
                for (Task value : tasks.values()) {
                    if (value.getTaskType().equals(TaskType.SUBTASK)) {
                        updateEpicStatus(id);
                    }
                }
            }
        }
    }

    // case 7: Изменить статус --------------------------------------------------
    @Override
    public void changeStatusTask(UUID id, Status status) {
        if (tasks.containsKey(id)) {
            if (tasks.get(id).getTaskType().equals(TaskType.SUBTASK)) {
                updateEpicStatus(tasks.get(id).getEpicId());
            }
            tasks.get(id).setStatus(status);
            System.out.println("Статус изменён");
        } else {
            System.out.println("Задача с таким идентификатором не найдена");
        }
    }

    // case 8: Получение списка всех подзадач определённого эпика. -----------------------------
    @Override
    public List<Task> getSubtaskList(UUID epicId) {
        List<Task> list = new ArrayList<>();
        for (UUID id : tasks.keySet()) {
            if (tasks.get(id).getId().equals(epicId)) {
                for (UUID subtaskUUID : tasks.get(epicId).getSubtasks()) { // итерация листа подзадач эпика
                    historyManager.add(tasks.get(subtaskUUID)); // добавляем в историю каждую подзадачу эпика (так как просмотриваются все подзадачи)
                    list.add(tasks.get(subtaskUUID));
                }
            }
        }
        return list;
    }

    // метод обновления статуса епика
    @Override
    public void updateEpicStatus(UUID id) {
        if (tasks.containsKey(id)) {
            boolean inProgress = true;
            // если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
            // *если у эпика нет подзадач
            if (tasks.get(id).getSubtasks().size() == 0) {
                tasks.get(id).setStatus(Status.NEW);
                inProgress = false;
            }
            // *или все они имеют статус NEW
            if (tasks.get(id).getSubtasks().size() != 0) { // проверить на ноль
                int counterNew = 0;

                for (int i = 0; i < tasks.get(id).getSubtasks().size(); i++) { // итерация листа с id подзадач
                    if (tasks.get(tasks.get(id).getSubtasks().get(i)).getStatus().equals(Status.NEW)) {
                        counterNew++;
                    }
                }
                if (tasks.get(id).getSubtasks().size() == counterNew) { // если 0? то значит новая
                    tasks.get(id).setStatus(Status.NEW);
                    inProgress = false;
                }
            }

            // если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
            if (tasks.get(id).getSubtasks().size() != 0) { // проверить на ноль
                int counterDone = 0;
                for (int i = 0; i < tasks.get(id).getSubtasks().size(); i++) { // перебор листа с id подзадач
                    if (tasks.get(tasks.get(id).getSubtasks().get(i)).getStatus().equals(Status.DONE)) {
                        counterDone++;
                    }
                }
                if (tasks.get(id).getSubtasks().size() == counterDone) {
                    tasks.get(id).setStatus(Status.DONE);
                    inProgress = false;
                }

                // во всех остальных случаях статус должен быть IN_PROGRESS
                if (inProgress) {
                    tasks.get(id).setStatus(Status.IN_PROGRESS);
                }
                tasks.put(tasks.get(id).getId(), tasks.get(id));
                System.out.println("Обновление списка эпика прошло успешно");
            }
        } else {
            System.out.println("Епик с таким id не найден");
        }
    }


    // case 9:
    public List<Task> getHistory() {
        return historyManager.getCustomLinkedList();
    }

    public HashMap<UUID, Task> getTasks() {
        return tasks;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}