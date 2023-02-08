package manager;
import exceptions.ManagerSaveException;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    String fileName;

    public FileBackedTasksManager(String fileName) {

        this.fileName = fileName;
    }

    public FileBackedTasksManager() {
    }

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static Gson gson = new Gson();

    public static void main(String[] args) throws IOException, InterruptedException {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager("tasksAndHistory.csv");
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        HttpServer httpServer = HttpServer.create();

        Epic epc1 = new Epic("Эпик 1", "написать спринт 7",
                LocalDateTime.of(2022, 8, 26, 10, 30), Duration.ofMinutes(30));
        SubTask task2 = new SubTask(" Подзадача 1", " проверить статус эпика", TaskStatus.DONE, 1,
                LocalDateTime.of(2022, 8, 27, 10, 30), Duration.ofMinutes(30));
        SubTask task3 = new SubTask(" Подзадача 2", " проверить статус эпика", TaskStatus.NEW, 1,
                LocalDateTime.of(2022, 8, 27, 11, 30), Duration.ofMinutes(15));
        Task task4 = new Task("Задача", "просто первая  задача в память", TaskStatus.NEW,
                LocalDateTime.of(2022, 8, 26, 10, 0), Duration.ofMinutes(60));
        Epic epc5 = new Epic("Эпик 5", "написать спринт 8",
                LocalDateTime.of(2022, 9, 26, 10, 30), Duration.ofMinutes(30));

        fileBackedTasksManager.addEpic(epc1);
        fileBackedTasksManager.addSubTask(task2);
        fileBackedTasksManager.addSubTask(task3);
        fileBackedTasksManager.addTask(task4);
        fileBackedTasksManager.addEpic(epc5);

        // Получили списки задач всех типов для сохранения истории в файле
        fileBackedTasksManager.getTask(4);
        fileBackedTasksManager.getTask(4);
        fileBackedTasksManager.getEpic(1);
        fileBackedTasksManager.getSubTask(2);
        fileBackedTasksManager.getSubTask(3);


        fileBackedTasksManager.printOllTasks();
        fileBackedTasksManager.printOllEpics();
        fileBackedTasksManager.printOllSubTasks();
        // Восстановили  разные типы задач и историю из файла

        FileBackedTasksManager main = new  FileBackedTasksManager("tasksAndHistory.csv");
        System.out.println(main.getPrioritizedTasks());

        FileBackedTasksManager taskManager1;
        taskManager1 = loadFromFile(new File("tasksAndHistory.csv"));
        InMemoryHistoryManager inMemoryHistoryManager1 = new InMemoryHistoryManager();
        System.out.println("\nИстория вызовов:");
        System.out.println(inMemoryHistoryManager1.getHistory());


        System.out.println("\nЗадачи всех типов:");
            System.out.println(taskManager1.getAllTasks());
        System.out.println("Теперь по отдельности");
        taskManager1.printOllTasks();
        taskManager1.printOllEpics();
        taskManager1.printOllSubTasks();

        System.out.println("\nЗадачи восстановились верно?");
        System.out.println(fileBackedTasksManager.getAllTasks().equals(taskManager1.getAllTasks()));
        System.out.println("\nИстория восстановились верно?");
        System.out.println(inMemoryHistoryManager.getHistory().equals(inMemoryHistoryManager1.getHistory()));

    }


    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic task) {
        super.addEpic(task);
        save();
    }

    @Override
    public String addSubTask(SubTask task) {
        super.addSubTask(task);
        save();
        return null;
    }

    @Override
    public Task getTask(int id) {
        super.getTask(id);
        save();
        return null;
    }

    @Override
    public Epic getEpic(int id) {
        super.getEpic(id);
        save();
        return null;
    }

    @Override
    public SubTask getSubTask(int id) {
        super.getSubTask(id);
        save();
        return null;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void apdateEpic(Epic epic) {
        super.apdateEpic(epic);
        save();
    }

    private String toString(Task task) {
        StringBuilder taskToString = new StringBuilder();
        taskToString.append(task.getId());
        taskToString.append(",");
        taskToString.append(task.getType());
        taskToString.append(",");
        taskToString.append(task.getTitle());
        taskToString.append(",");
        taskToString.append(task.getStatus());
        taskToString.append(",");
        taskToString.append(task.getDescription());
        taskToString.append(",");
        taskToString.append(task.getStartTimeTask());
        taskToString.append(",");
        taskToString.append(task.getDuration());
        taskToString.append(",");
        if (task instanceof SubTask) {
            taskToString.append(((SubTask) task).getEpicId());
        }
        return taskToString.toString();
    }

    private static String historyToString(List<Task> list) {
        StringBuilder history = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            history.append(list.get(i).getId());
            history.append(",");
        }
        return history.toString();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("id,type,name,status,description,duration,startTime,epic\n");
            List<Task> tasks = getAllTasks();

            for (Task task : tasks) {
                writer.write(toString(task));
                writer.write("\n");
            }
            writer.write("\n");
            InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
            writer.write(historyToString(inMemoryHistoryManager.getHistory()));
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    public Task fromString(String value) {
        Task newTask = null;
        String[] words = value.split(",");

        Type newType = Type.valueOf(words[1]);

        switch (newType) {
            case TASK:
                newTask = new Task(
                        words[2],
                        words[4], TaskStatus.valueOf(words[3]), LocalDateTime.parse(words[5]), Duration.parse(words[6]));
                break;
            case EPIC:
                newTask = new Epic(words[2],
                        words[4], LocalDateTime.parse(words[5]), Duration.parse(words[6]));
                break;
            case SUBTASK:
                newTask = new SubTask(words[2],
                        words[4], TaskStatus.valueOf(words[3]), (Integer.parseInt(words[7])),
                        LocalDateTime.parse(words[5]), Duration.parse(words[6]));

        }
        return newTask;
    }

    private static List<Integer> historyFromString(String value) {
        String[] temp = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (String s : temp) {
            history.add(Integer.parseInt(s));
        }
        return history;
    }

    private void restoreHistory(String history) {
        List<Integer> historyList = historyFromString(history);
        for (Integer id : historyList) {
            super.getTask(id);
            super.getEpic(id);
            super.getSubTask(id);
        }
    }

    private void restoreMaps(String[] tasks) {
        for (int i = 1; i < tasks.length; i++) {
            Task task = fromString(tasks[i]);
            if (task instanceof Epic) {
                apdateEpic((Epic) task);
            } else if (task instanceof SubTask) {
                updateSubTask((SubTask) task);
            } else {
                updateTask(task);
            }
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file.getName());
        String temp;
        try {
            temp = Files.readString(Path.of(file.getPath()));
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
        String[] tasksAndHistory = temp.split("\n\n");
        String[] tasks = tasksAndHistory[0].split("\n");
        fileBackedTasksManager.restoreMaps(tasks);
        if (tasksAndHistory.length > 1) {
            String history = tasksAndHistory[1];
            fileBackedTasksManager.restoreHistory(history);
        }

        fileBackedTasksManager.save();
        return fileBackedTasksManager;

    }


}

