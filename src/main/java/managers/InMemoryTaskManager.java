package main.java.managers;

import main.java.intefaces.HistoryManager;
import main.java.intefaces.TaskManager;
import main.java.service.Status;
import main.java.service.TaskType;
import main.java.tasks.Epic;
import main.java.tasks.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {
    private final Map<UUID, Task> tasks = new HashMap<>();
    private Set<Task> prioritizedTasks = new TreeSet<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void addNewTask(Task task) {
        task.setId(java.util.UUID.randomUUID());
        try {
            if (!tasks.isEmpty()) {
                for (Task taskInMap : tasks.values()) {
                    if (!(taskInMap.getTaskType().equals(TaskType.EPIC) || task.getTaskType().equals(TaskType.EPIC))) {
                        task.setEndTime(task.getStartTime().plusMinutes(task.getDuration()));

                        if (checkTime(task) != null) {
                            return;
                        }

                    }
                }
            }

            switch (task.getTaskType()) {
                case TASK:
                    tasks.put(task.getId(), task);
                    System.out.println("Задача успешно добавлена");
                    break;
                case EPIC:
                    tasks.put(task.getId(), task);
                    System.out.println("Эпик успешно добавлен");
                    break;
                case SUBTASK:
                    tasks.put(task.getId(), task);
                    System.out.println("Подзадача успешно добавлена");

                    Epic epic = (Epic) tasks.get(task.getEpicId());
                    epic.setSubtasks(task.getId());
                    if (epic.getStartTime() == null) {
                        epic.setStartTime(task.getStartTime());
                    }
                    epic.setDuration(epic.getDuration() + task.getDuration());
                    epic.setEndTime(epic.getStartTime().plusMinutes(epic.getDuration()));

                    updateTask(epic);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private String checkTime(Task task) {
        String anotherTimeToPick = null;
        for (Task taskInMap : tasks.values()) {
            if (
                    (taskInMap.getStartTime().isBefore(task.getStartTime())
                            && taskInMap.getEndTime().isAfter(task.getStartTime()))
                            || (taskInMap.getStartTime().isBefore(task.getEndTime())
                            && taskInMap.getEndTime().isAfter(task.getEndTime()))
            ) {
                System.out.println("Пожалуйста выберете другое стартовое время");
            }
        }
        return anotherTimeToPick;
    }

    // case 2: Получение списка всех задач.-------------------------------------
    @Override
    public List<Task> getAllTasksByTaskType(TaskType taskType) {
        List<Task> list;
        if (!tasks.isEmpty()) {
            list = tasks.entrySet().stream().filter(t -> t.getValue()
                            .getTaskType().equals(taskType))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());

        } else {
            return Collections.emptyList();
        }
        return list;
    }

    // case 3: Удаление всех задач по типу.---------------------------------------
    @Override
    public void removeTasksByTasktype(TaskType taskType) {
        if (taskType.equals(TaskType.SUBTASK)) {
            tasks.values().stream().forEach(t -> t.getSubtasks().clear());
            tasks.values().stream().forEach(t -> historyManager.remove(t.getId()));

            LocalDateTime defaultTime = LocalDateTime.parse("2000-01-01 00:00:00",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US));

            tasks.values().stream()
                    .filter(t -> t.getTaskType().equals(TaskType.EPIC))
                    .forEach(t -> t.setStartTime(defaultTime));
            tasks.values().stream()
                    .filter(t -> t.getTaskType().equals(TaskType.EPIC))
                    .forEach(t -> t.setEndTime(defaultTime));
            tasks.values().stream()
                    .filter(t -> t.getTaskType().equals(TaskType.EPIC))
                    .forEach(t -> t.setDuration(0));
        }
        tasks.entrySet().removeIf(entry -> taskType.equals(entry.getValue().getTaskType()));

    }

    // case 4:get методы-------------------------------------------------------------
    @Override
    public Task getTask(UUID idInput) {
        Task task = null;

        if (tasks.containsKey(idInput)) {
            task = tasks.get(idInput);
            historyManager.add(task);
            System.out.println(tasks.get(idInput));
        } else if (tasks.isEmpty()) {
            System.out.println("Мапа пуста");
        } else {
            System.out.println("Неверный идентификатор задачи");
        }
        return task;
    }

    // case 5: Обновление.
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
        Epic epic;
        try {
            if (tasks.get(id).getTaskType().equals(TaskType.EPIC)) {

                tasks.get(id).removeSubtask(id);
                tasks.get(id).cleanSubtaskIds();
                for (Task subtask : getSubtasksFromEpic(id)) {
                    tasks.remove(subtask.getId());
                }
            }

            tasks.keySet().removeIf(u -> u.equals(id)); // Predicate

            historyManager.remove(id);
            System.out.println("Задача удалена");

            if (tasks.containsKey(id)) {
                if (tasks.get(id).getTaskType().equals(TaskType.SUBTASK)) {
                    for (Task value : tasks.values()) {
                        if (value.getTaskType().equals(TaskType.SUBTASK)) {
                            updateEpicStatus(id);
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            throw new NullPointerException("Неверный идентификатор задачи");
        }
    }

    // case 7: Изменить статус --------------------------------------------------
    @Override
    public void changeStatusTask(UUID id, Status status) {
        try {

            if (tasks.get(id).getTaskType().equals(TaskType.EPIC)) {
                System.out.println("Статус Эпика зависит от статусов его подзадач(и) и самому изменить невозможно");
                return;
            }
            if (tasks.get(id).getTaskType().equals(TaskType.SUBTASK)) {
                tasks.get(id).setStatus(status);
                updateEpicStatus(tasks.get(id).getId());
            } else {
                tasks.get(id).setStatus(status);
            }
            System.out.println("Статус изменён");
        } catch (NullPointerException e) {
            throw new NullPointerException("Неверный идентификатор задачи");
        }
    }

    // case 8: Получение списка всех подзадач определённого эпика. -----------------------------
    @Override
    public List<Task> getSubtasksFromEpic(UUID epicId) {
        List<Task> subtasks = new ArrayList<>();
        try {
            if (tasks.isEmpty()) {
                System.out.println("Мапа пуста");
            } else {
                for (UUID subtaskUUID : tasks.get(epicId).getSubtasks()) {
                    historyManager.add(tasks.get(subtaskUUID));
                    subtasks.add(tasks.get(subtaskUUID));
                }
            }
        } catch (NullPointerException e) {
            throw new NullPointerException("Неверный идентификатор задачи");
        }
        return subtasks;
    }

    // case 9: получение списка просмотренных задач
    @Override
    public List<Task> getHistory() {
        return historyManager.getTasksInHistory();
    }

    // case 11: сортировка задач по стартовому времени
    @Override
    public void prioritizeTasks() {
        if (tasks.size() > 1) {
            prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
            prioritizedTasks.addAll(Stream.of(tasks.values())
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()));
        } else {
            System.out.println("Нужно ещё больше задач");
        }
    }

    // ==========================   Getters       ==========================
    @Override
    public Set<Task> getPrioritizedTasks() {
        prioritizeTasks();
        return prioritizedTasks;
    }

    public Map<UUID, Task> getTasks() {
        return tasks;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }
// ==========================   Getters End   ==========================

    public void updateEpicStatus(UUID id) {
        UUID epicId = tasks.get(id).getEpicId();
        List<UUID> uuidsSubtasks = new ArrayList<>(tasks.get(epicId).getSubtasks());
//        ArrayList<Status> subtasks = new  ArrayList<>();
        int done = 0;
        int inProgress = 0;
        int newTask = 0;

        for (UUID uuidsSubtask : uuidsSubtasks) {

            Status status = tasks.get(uuidsSubtask).getStatus();
            switch (status) {
                case NEW:
                    newTask++;
                    break;
                case IN_PROGRESS:
                    inProgress++;
                    break;
                case DONE:
                    done++;
                    break;
            }
        }


        if (newTask < tasks.get(epicId).getSubtasks().size()) {
            tasks.get(epicId).setStatus(Status.IN_PROGRESS);
        } else if (done == tasks.get(epicId).getSubtasks().size()) {
            tasks.get(epicId).setStatus(Status.DONE);
        } else {
            tasks.get(epicId).setStatus(Status.NEW);
        }
        System.out.println("Обновление списка эпика прошло успешно");
    }
}









