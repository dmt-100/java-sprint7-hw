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
    private final Map<UUID, Task> tasks = new HashMap<>(); // переделать на treemap
    private Set<Task> prioritizedTasks = new TreeSet<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    /*
    Из условий в ТЗ-3: <Иногда для выполнения какой-нибудь масштабной задачи её лучше разбить на подзадачи (англ. subtask). Большую задачу, которая делится на подзадачи, мы будем называть эпиком (англ. epic).>
    Не сказано что будет формироваться первыми эпик или подзадача, предложу что эпик первый, тогда стартовое время эпику устанавливается впоследсвии создания подзадачи
     */

    /* не совсем понятна логика в задании про окончание времени эпика, где сказано:
     <а время завершения — время окончания самой поздней из задач.>, по идее время завершения должна быть сумма времени всех подзадач прибавленной к началу времени эпика или первой подзадачи, тем не менее сделал как написано в ТЗ
    */
    @Override
    public void addNewTask(Task task) {
        if (!task.getTaskType().equals(TaskType.EPIC)) { // условие только для тестов после убрать
            task.setId(java.util.UUID.randomUUID());
        }
        try {
            if (!tasks.isEmpty()) {
                for (Task taskInMap : tasks.values()) {
                    if (!(taskInMap.getTaskType().equals(TaskType.EPIC) || task.getTaskType().equals(TaskType.EPIC))) {
                        task.setEndTime(task.getStartTime().plusMinutes(task.getDuration()));
                        if (
                                (taskInMap.getStartTime().isBefore(task.getStartTime())
                                        && taskInMap.getEndTime().isAfter(task.getStartTime()))
                                        || (taskInMap.getStartTime().isBefore(task.getEndTime()) //true
                                        && taskInMap.getEndTime().isAfter(task.getEndTime()))
                        ) {
                            System.out.println("Пожалуйста выберете другое стартовое время");
                            return;
                        }
                    }
                }
            }

            switch (task.getTaskType()) { // switch-case для удобства читаемости
                case TASK:
//                task.setId(java.util.UUID.randomUUID());
//                task.setEndTime(task.getStartTime().plusMinutes(task.getDuration()));
                    tasks.put(task.getId(), task);
                    System.out.println("Задача успешно добавлена");
                    break;
                case EPIC:
//                    task.setId(java.util.UUID.randomUUID()); // временно для тестов в abstract class TaskManager после убрать
                    tasks.put(task.getId(), task);
                    System.out.println("Эпик успешно добавлен");
                    break;
                case SUBTASK:
//                task.setId(java.util.UUID.randomUUID());
//                task.setEndTime(task.getStartTime().plusMinutes(task.getDuration()));
                    tasks.put(task.getId(), task);
                    System.out.println("Подзадача успешно добавлена");

                    Epic epic = (Epic) tasks.get(task.getEpicId()); // нашел решение только через кастинг
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
            tasks.values().stream().forEach(t -> t.getSubtasks().clear()); // удаление списка подзадач у Эпиков
            tasks.values().stream().forEach(t -> historyManager.remove(t.getId())); // удаление подзадач в истории

            LocalDateTime defaultTime = LocalDateTime.parse("2000-01-01 00:00:00",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)); // 2014-12-22T05:10:30

            tasks.values().stream() // обнуление времени у эпиков после удаления подзадач
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

    // ТЗ-4 Работает с History
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
    public short removeTaskById(UUID id) {
        Epic epic;
        try {
            if (tasks.get(id).getTaskType().equals(TaskType.EPIC)) {

                tasks.get(id).removeSubtask(id);
                tasks.get(id).cleanSubtaskIds();
                for (Task subtask : getSubtasksFromEpic(id)) {
                    if (tasks.containsKey(subtask.getId())) {
                        tasks.remove(subtask.getId()); // удаление сабтасков епика из мапы
                    }
                }
            }

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

        } catch (NullPointerException e) {
            throw new NullPointerException("Неверный идентификатор задачи");
        }
        return 0;
    }

    // case 7: Изменить статус --------------------------------------------------
    @Override
    public void changeStatusTask(UUID id, Status status) {
        try {
            if (tasks.get(id).getTaskType().equals(TaskType.SUBTASK)) {
                updateEpicStatus(tasks.get(id).getEpicId());
            }
            tasks.get(id).setStatus(status);
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
//                for (UUID id : tasks.keySet()) { // ТЗ-7 после удалить
//                    if (tasks.get(id).getId().equals(epicId)) {
                for (UUID subtaskUUID : tasks.get(epicId).getSubtasks()) { // итерация листа подзадач эпика
                    historyManager.add(tasks.get(subtaskUUID)); // добавляем в историю каждую подзадачу эпика (так как просматриваются все подзадачи)
                    subtasks.add(tasks.get(subtaskUUID));
                }
//                    }
//                }
            }
        } catch (NullPointerException e) {
            throw new NullPointerException("Неверный идентификатор задачи");
        }
        return subtasks;
    }

    // case 9:
    @Override
    public List<Task> getHistory() {
        return historyManager.getTasksInHistory();
    }

    // case 11:
    @Override
    public void prioritizeTasks() { // ТЗ-7 так как сет, то те задачи у которых одинаковое время будут перезаписываться, что бы этого избежать нужно чтобы стартовое время отличалось или просто вводить по одной задачи в мейне или заменить .now()
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        prioritizedTasks.addAll(Stream.of(tasks.values())
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
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


    // метод только этого класса
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
}