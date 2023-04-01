package main.java.managers;

import main.java.intefaces.HistoryManager;
import main.java.intefaces.TaskManager;
import main.java.service.Status;
import main.java.service.TaskType;
import main.java.tasks.Epic;
import main.java.tasks.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {
    private final Map<UUID, Task> tasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public UUID addNewTask(Task task) {
        task.setId(java.util.UUID.randomUUID()); // ТЗ-7 EpicTest после тестов снять (перезаписывает)
        if (task.getTaskType().equals(TaskType.EPIC)) {
            tasks.put(task.getId(), task);
            System.out.println("Задача успешно добавлена");
        } else { // временно из-за Назначения Id эпика
            tasks.put(task.getId(), task);
            if (task.getTaskType().equals(TaskType.SUBTASK)) {

                Epic epic = (Epic) tasks.get(task.getEpicId()); // нашел решение только через кастинг

/* не совсем понятна логика в задании про окончание времени эпика, где сказано:
 <а время завершения — время окончания самой поздней из задач.>, по идее время завершения должна быть сумма всех подзадач
 прибавленной к началу времени эпика или первой подзадачи, тем не менее сделал как написано в ТЗ
*/
                if (epic.getSubtasks().isEmpty()) { // если список подзадач у эпика пуст, то сразу устанавливаем
                    epic.setStartTime(task.getStartTime()); // ТЗ-7 устанавливаем стартовое время для эпика
                    epic.setDuration(task.getDuration());
                } else { // иначе прибавляем время и продолжительность к уже установленному
                    int duration;
                    LocalDateTime endTime;
                    duration = epic.getDuration() + task.getDuration();
                    epic.setDuration(duration);// ТЗ-7 add duration to epic
//                    endTime = epic.getStartTime().plusMinutes(epic.getDuration());
                    endTime = task.getEndTime();
                    epic.setEndTime(endTime); // ТЗ-7 epic set endTime
                }

                epic.getSubtasks().add(task.getId());
                updateTask(epic);
            }
        }
        return task.getId();

    }

    // case 2: Получение списка всех задач.-------------------------------------
    @Override
    public List<Task> getAllTasksByTaskType(TaskType taskType) {
        List<Task> list = new ArrayList<>();
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
        }
        tasks.entrySet().removeIf(entry -> taskType.equals(entry.getValue().getTaskType()));
    }

    // ТЗ-4
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
    public void removeTaskById(UUID id) {
        try {

            if (tasks.get(id).getTaskType().equals(TaskType.EPIC)) {
                for (Task subtask : getSubtasksFromEpic(id)) {
                    tasks.remove(subtask.getId()); // удаление сабтасков епика из мапы
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

    public Map<UUID, Task> getTasks() {
        return tasks;
    }

    // case 11:
//    @Override
//    public List<Task> getPrioritizedTasks() {
//        List<Task> list = new ArrayList<>();
////        Collections.checkedSortedMap()
////
////        return null;
//        return list;
//    }

    // case 11:
    public List<Task> getPrioritizedTasks() {
        Set<Task> set = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        set.addAll(Stream.of(tasks.values())
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
        return new ArrayList<>(set);

//        List<Task> tasks = new ArrayList<>();
//        for (Task task : this.tasks.values()) {
//  //          if (task.getTaskType() != TaskType.EPIC) { // Эпики как понимаю не записываем в сортированный список, но это не точно
//                tasks.add(task);
//  //          }
//        }
//
//        List<Task> sortedTasks = tasks.stream()
////                .sorted((t1, t2) -> t1.getStartTime().compareTo(t2.getStartTime()))
//                .filter(task -> task.getStartTime() != null)
//                .sorted(Comparator.comparing(Task::getStartTime))
//                .collect(Collectors.toList());
//
//        for (Task sortedTask : sortedTasks) {
//            System.out.println(sortedTask);
//        }
//        return sortedTasks;
    }
//        List<Map.Entry<UUID, Task>> list = new ArrayList<>(map.entrySet());
//        list.sort(Map.Entry.comparingByValue());
//
//        Map<UUID, Task> result = new LinkedHashMap<>();
//        for (Map.Entry<UUID, Task> entry : list) {
//            result.put(entry.getKey(), entry.getValue());
//        }


    public HistoryManager getHistoryManager() {
        return historyManager;
    }


    public void setStartTimeToEpic() {

    }
}