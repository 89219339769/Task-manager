package tests;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    @Test
    void addTask() {
        InMemoryTaskManager taskManager =new InMemoryTaskManager();
        Epic epc1 = new Epic("Эпик 1", "написать спринт 7",
                LocalDateTime.of(2022, 8, 26, 10, 30), Duration.ofMinutes(30));
        SubTask task2 = new SubTask(" Подзадача 1", " проверить статус эпика", TaskStatus.DONE, 0,
                LocalDateTime.of(2022, 8, 27, 10, 30), Duration.ofMinutes(30));
        SubTask task3 = new SubTask(" Подзадача 2", " проверить статус эпика", TaskStatus.NEW, 0,
                LocalDateTime.of(2022, 8, 27, 11, 30), Duration.ofMinutes(15));
        Task task4 = new Task("Задача", "просто первая  задача в память", TaskStatus.NEW,
                LocalDateTime.of(2022, 8, 26, 10, 0), Duration.ofMinutes(60));


        taskManager.addTask(task4);
        assertEquals(1,   taskManager.getAllTasks().size());
    }

    @Test
    void addEpic() {
        InMemoryTaskManager taskManager =new InMemoryTaskManager();
        Epic epc1 = new Epic("Эпик 1", "написать спринт 7",
                LocalDateTime.of(2022, 8, 26, 10, 30), Duration.ofMinutes(30));
         taskManager.addEpic(epc1);
        assertEquals(1,   taskManager.getAllTasks().size());
    }

    @Test
    void ddSubTask() {
        InMemoryTaskManager taskManager =new InMemoryTaskManager();
        Epic epc1 = new Epic("Эпик 1", "написать спринт 7",
                LocalDateTime.of(2022, 8, 26, 10, 30), Duration.ofMinutes(30));
        SubTask task2 = new SubTask(" Подзадача 1", " проверить статус эпика", TaskStatus.DONE, 0,
                LocalDateTime.of(2022, 8, 27, 10, 30), Duration.ofMinutes(30));

         taskManager.addEpic(epc1);
        taskManager.addSubTask(task2);
        assertEquals(1,   taskManager.subTaskMap.size());
    }
}