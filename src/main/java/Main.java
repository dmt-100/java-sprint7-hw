package main.java;

import main.java.managers.FileBackedTasksManager;
import main.java.service.*;
import main.java.tasks.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private static final String sep = File.separator;
    private static final String saveTasksFilePath = String.join(sep, "src", "main", "java", "resources", "taskSaves" + ".csv");
    private static final File file = new File(saveTasksFilePath);
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);

        Task task1 = new Task(TaskType.TASK, "Переезд", "Собрать коробки", Status.NEW, LocalDateTime.now(), 50);
//        Task task2 = new Task(TaskType.TASK, "Переезд", "Упаковать кошку", Status.NEW, LocalDateTime.now(), 5);
//        Task task3 = new Task(TaskType.TASK, "Переезд", "Сказать слова прощания", Status.NEW, LocalDateTime.now(), 2);
//        Task task4 = new Task(TaskType.TASK, "Переезд", "Собрать коробки", Status.NEW, LocalDateTime.now(), 50);
//        Task task5 = new Task(TaskType.TASK, "Переезд", "Упаковать кошку", Status.NEW, LocalDateTime.now(), 5);
//        Task task6 = new Task(TaskType.TASK, "Переезд", "Сказать слова прощания", Status.NEW, LocalDateTime.now(), 2);

        List<UUID> subtasksList = new ArrayList<>();
        Epic epic1 = new Epic(UUID.randomUUID(), TaskType.EPIC, "Переезд", "Переезд", Status.NEW,
                subtasksList);
//        Epic epic2 = new Epic(UUID.randomUUID(), TaskType.EPIC, "Переезд2", Status.NEW, "Переезд2", subtasksList);

        Subtask subtask1 = new Subtask(TaskType.SUBTASK, "тест1",
                "Собрать коробки", Status.NEW, LocalDateTime.now(), 50);
        Subtask subtask2 = new Subtask(TaskType.SUBTASK, "тест2",
                "Упаковать кошку", Status.NEW, LocalDateTime.now(), 5);
//        Subtask subtask3 = new Subtask(TaskType.SUBTASK, "тест3", Status.NEW,
//                "Сказать слова прощания", epic1.getId());

        boolean menu = true;
        while (menu) {
            printMenu();

            int userInput = scanner.nextInt();
            switch (userInput) {
                case 1: // Получение всех задач
                    printMenuCase1();
                    int userInputCase1 = scanner.nextInt();
                    switch (userInputCase1) {
                        case 1:
                            fileBackedTasksManager.addNewTask(new Task(TaskType.TASK, "Переезд",
                                    "Собрать коробки",Status.NEW, LocalDateTime.now(), 50));
                            fileBackedTasksManager.addNewTask(new Task(TaskType.TASK, "Переезд",
                                    "Упаковать кошку", Status.NEW, LocalDateTime.now(), 5));
                            fileBackedTasksManager.addNewTask(new Task(TaskType.TASK, "Переезд",
                                    "Сказать слова прощания", Status.NEW, LocalDateTime.now(), 2));
                            fileBackedTasksManager.addNewTask(new Task(TaskType.TASK, "Переезд",
                                    "Собрать коробки", Status.NEW, LocalDateTime.now(), 50));
                            fileBackedTasksManager.addNewTask(new Task(TaskType.TASK, "Переезд",
                                    "Упаковать кошку", Status.NEW, LocalDateTime.now(), 5));
                            break;
                        case 2:
                            fileBackedTasksManager.addNewTask(new Epic(UUID.randomUUID(), TaskType.EPIC,
                                    "Переезд1", "Переезд1", Status.NEW, subtasksList));
//                            fileBackedTasksManager.addNewTask(new Epic(UUID.randomUUID(), TaskType.EPIC,
//                                    "Переезд2", "Переезд2", Status.NEW, subtasksList));
//                            fileBackedTasksManager.addNewTask(new Epic(UUID.randomUUID(), TaskType.EPIC,
//                                    "Переезд3", "Переезд3", Status.NEW, subtasksList));
                            break;
                        case 3:
                            // нужно внести Id эпика в подзадачу, чтобы привязать подзадачу к Эпику
                            // точно не помню как в задании, нужно их перечитать
                            // пока что это тесты для вычисления правильного сложения времени эпика
                            System.out.println("Введите идентификатор Эпика для внесения данной подзадачи");
                            UUID epicId = UUID.fromString(scanner.next());
                            subtask1.setEpicId(epicId);
                            System.out.println("Введите идентификатор Эпика для внесения данной подзадачи");
                            UUID epicId2 = UUID.fromString(scanner.next());
                            subtask2.setEpicId(epicId2);

                            fileBackedTasksManager.addNewTask(subtask1);
                            fileBackedTasksManager.addNewTask(subtask2);
//                            fileBackedTasksManager.addNewTask(subtask2);
//                            fileBackedTasksManager.addNewTask(subtask3);
                            break;
                    }
                    break;

                case 2: // Получение всех задач
                    printMenuCase2();
                    TaskType taskType2;
                    int userInputCase2 = scanner.nextInt();
                    switch (userInputCase2) {
                        case 1:
                            taskType2 = TaskType.TASK;
                            System.out.println(fileBackedTasksManager.getAllTasksByTaskType(taskType2));
                            break;
                        case 2:
                            taskType2 = TaskType.EPIC;
                            System.out.println(fileBackedTasksManager.getAllTasksByTaskType(taskType2));
                            break;
                        case 3:
                            taskType2 = TaskType.SUBTASK;
                            System.out.println(fileBackedTasksManager.getAllTasksByTaskType(taskType2));
                            break;
                    }
                    break;

                case 3: // Удаление всех задач
                    printMenuCase3();
                    TaskType taskType3;
                    int userInputCase3 = scanner.nextInt();
                    switch (userInputCase3) {
                        case 1:
                            taskType3 = TaskType.TASK;
                            fileBackedTasksManager.removeTasksByTasktype(taskType3);
                            break;
                        case 2:
                            taskType3 = TaskType.EPIC;
                            fileBackedTasksManager.removeTasksByTasktype(taskType3);
                            break;
                        case 3:
                            taskType3 = TaskType.SUBTASK;
                            fileBackedTasksManager.removeTasksByTasktype(taskType3);
                            break;
                    }
                    break;

                case 4: // получение по id
                    System.out.println("Введите номер идентификатора");
                    UUID taskId = UUID.fromString(scanner.next());
                    fileBackedTasksManager.getTask(taskId);

                    break;

                case 5: // обновление по id Посмотреть ТЗ по Id или сама задачи
                    printMenuCase5();
                    int userInputCase5 = scanner.nextInt();
                    System.out.println("Введите идентификатор той задачи которую хотите обновить");
                    String taskIdCase5 = scanner.next();
                    switch (userInputCase5) {
                        case 1:
                            task1.setDescription("Сказать слова прощания test Case5");
                            fileBackedTasksManager.updateTask(task1);
                            break;
                        case 2:

                            break;
                        case 3:
                            // Помимо id задачи который хотим заменить нужен также id эпика для обновление списка
                            // поэтому подразумевается что епик уже занесен в мапу
                            Subtask subtaskTest2 = new Subtask(TaskType.SUBTASK, "тест1",
                                    "Собрать коробки", Status.NEW, epic1.getId(), LocalDateTime.now(), 50); // исправить
//                            fileBackedTasksManager.updateSubtask(subtaskTest2);
                            break;
                    }
                    break;

                case 6: // Удаление по идентификатору.
                    System.out.println("Введите идентификатор для удаления");
                    UUID id = UUID.fromString(scanner.next());
                    fileBackedTasksManager.removeTaskById(id);
                    break;

                case 7: // Изменить статус
                    System.out.println("Введите id задачи, чей статус хотите поменять");
                    UUID statusId = UUID.fromString(scanner.next());
                    System.out.println("Назначьте статус, где:\n1 - Задача новая\n" + "2 - Задача выполнена\n3 - Задача в действии");
                    int check = scanner.nextInt();
                    Status status = null;
                    switch (check) {
                        case 1:
                            status = Status.NEW;
                            break;
                        case 2:
                            status = Status.DONE;
                            break;
                        case 3:
                            status = Status.IN_PROGRESS;
                            break;
                    }
                    fileBackedTasksManager.changeStatusTask(statusId, status);
                    break;

                case 8: // Получение списка всех подзадач определённого эпика.
                    System.out.println("Получение списка всех подзадач определённого эпика\n" + "Введите id эпика, чтобы получить его подзадачи:");
                    UUID epicId = UUID.fromString(scanner.next());
                    fileBackedTasksManager.getSubtasksFromEpic(epicId).forEach(System.out::println);
                    break;

                // ТЗ-4
                case 9: // Информация по просмотрам.
                    System.out.println("Какие задачи были просмотрены:");
                    fileBackedTasksManager.getHistory().forEach(System.out::println);
                    break;

                case 10: // Сохранить в файл
//                    fileBackedTasksManager.save();
//                    System.out.println("test3 getViewedTasks(): " + fileBackedTasksManager.historyManager.getCustomLinkedList());
//                    fileBackedTasksManager.historyManager.getCustomLinkedList().stream()
//                            .forEach(task -> {System.out.println(task.getId());});
                    break;

                case 11:
                    fileBackedTasksManager.getPrioritizedTasks();
                    for (Task task : fileBackedTasksManager.getPrioritizedTasks()) { //проверка
                        System.out.println(task);
                    }

                    break;

                case 0: // Выход
                    menu = false;
                    break;
            }

        }
    }

    public static void printMenu() {
        System.out.println("1 - Добавить новую задачу");
        System.out.println("2 - Получить список всех задач");
        System.out.println("3 - Удалить все задачи");
        System.out.println("4 - Посмотреть задачу по идентификатору");
        System.out.println("5 - Обновить задачу по идентификатору");
        System.out.println("6 - Удалить задачу по идентификатору");
        System.out.println("7 - Изменить статус задачи");
        System.out.println("8 - Получение списка всех подзадач определённого эпика");
        System.out.println("9 - Информация по просмотрам задач");
        System.out.println("10 - Сохранить задачи");
        System.out.println("11 - Сортировать задачи по началу времени");

        System.out.println("0 - Выход");
    }

    public static void printMenuCase1() {
        System.out.println("1 - Добавить новую задачу");
        System.out.println("2 - Добавить новый эпик");
        System.out.println("3 - Добавить новую подзадачу");
    }

    public static void printMenuCase2() {
        System.out.println("1 - Получить все задачи");
        System.out.println("2 - Получить все эпики");
        System.out.println("3 - Получить все подзадачи");
    }

    public static void printMenuCase3() {
        System.out.println("1 - Удалить все задачи");
        System.out.println("2 - Удалить все эпикови");
        System.out.println("3 - Удалить все подзадачи");
    }

    public static void printMenuCase5() {
        System.out.println("1 - Обновление задачи по идентификатору");
        System.out.println("2 - Обновление епика по идентификатору");
        System.out.println("3 - Обновление подзадачи по идентификатору");
    }

}