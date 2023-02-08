import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;

import manager.InMemoryHistoryManager;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;
import manager.InMemoryTaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;




public class Main {




    public static void main(String[] args) throws IOException {

        TaskManager taskManager = new InMemoryTaskManager();
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();






        Epic epc1 = new Epic("Эпик 1", "написать спринт 7",
                LocalDateTime.of(2022, 3, 26, 10, 30), Duration.ofMinutes(30));
        SubTask task2 = new SubTask(" Подзадача 1", " проверить статус эпика", TaskStatus.DONE, 0,
                LocalDateTime.of(2022, 8, 27, 10, 30), Duration.ofMinutes(30));
        SubTask task3 = new SubTask(" Подзадача 2", " проверить статус эпика", TaskStatus.NEW, 0,
                LocalDateTime.of(2022, 1, 27, 11, 30), Duration.ofMinutes(15));


// проверили что продолжительность и начало выполнения эпика меняются при добавлении подзадач
        Task task4 = new Task("Задача1", "просто первая  задача в память", TaskStatus.NEW,
                LocalDateTime.of(2022, 8, 1, 10, 0), Duration.ofMinutes(60));
        Task task5 = new Task("Задача2", "просто вторая  задача в память", TaskStatus.NEW,
                LocalDateTime.of(2022, 7, 1, 10, 10), Duration.ofMinutes(90));

        //     Задачи добавляются только в случае если они не пересекаются по времени выполнения
        taskManager.addEpic(epc1);
        taskManager.addTask(task4);
        taskManager.addTask(task5);

        // проверяем правильное удаление задач разных типов
        taskManager.deleteTask(1);
        taskManager.addSubTask(task3);

        taskManager.deleteEpic(0);
        taskManager.deleteSubTask(3);

    }
}