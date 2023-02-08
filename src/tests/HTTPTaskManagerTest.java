package tests;

import manager.HTTPTaskManager;
import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HTTPTaskManagerTest {

    @BeforeEach
    void createTasks() {
        HTTPTaskManager hTTPTaskManager = new HTTPTaskManager();
        Task task4 = new Task("Задача1", "просто первая  задача в память", TaskStatus.NEW,
                LocalDateTime.of(2022, 8, 1, 10, 0), Duration.ofMinutes(60));
        Task task5 = new Task("Задача2", "просто вторая  задача в память", TaskStatus.NEW,
                LocalDateTime.of(2022, 7, 1, 10, 10), Duration.ofMinutes(90));
        Epic epc1 = new Epic("Эпик 1", "написать спринт 7",
                LocalDateTime.of(2022, 3, 26, 10, 30), Duration.ofMinutes(30));

        hTTPTaskManager.addTask(task4);
        hTTPTaskManager.addTask(task5);
        hTTPTaskManager.addEpic(epc1);
    }

    @Test
    public void addTask() {
       HTTPTaskManager hTTPTaskManager = new HTTPTaskManager();
        assertEquals(2,   hTTPTaskManager.getAllTasks().size());
        }


    @Test
    public void deleteTask() {
        HTTPTaskManager hTTPTaskManager = new HTTPTaskManager();
        hTTPTaskManager.deleteTask(1);
        assertEquals(1,   hTTPTaskManager.getAllTasks().size());
    }


    @Test
    public void getTaskById() {
        HTTPTaskManager hTTPTaskManager = new HTTPTaskManager();
        hTTPTaskManager.getTask(1);
        String exp = "Задача1 просто первая  задача в память, id=1, Статус=NEW приступить к выполнению 2022-08-01T10:00 ," +
                "продолжительность задачи PT1H минут";
        String temp = String.valueOf(hTTPTaskManager.getTask(1));
        assertEquals(temp ,   exp);
    }


    @Test
    public void getPrioritizedTasks(){
        HTTPTaskManager hTTPTaskManager = new HTTPTaskManager();
        String exp = "[Задача2 просто вторая  задача в память, id=2, " +
                "Статус=NEW приступить к выполнению 2022-07-01T10:10 ,продолжительность задачи PT1H30M минут," +
                " Задача1 просто первая  задача в память, id=1, Статус=NEW приступить к выполнению 2022-08-01T10:00 ," +
                "продолжительность задачи PT1H минут]";
        String temp = String.valueOf( hTTPTaskManager.getPrioritizedTasks());
        assertEquals(temp, exp);

    }


    @Test
    public void getAllSubTasks(){
        HTTPTaskManager hTTPTaskManager = new HTTPTaskManager();
        String exp = "[]";
        String temp = String.valueOf( hTTPTaskManager.getAllSubTasks());
        assertEquals(temp, exp);
    }


    @Test
    public void getAllEpics(){
        HTTPTaskManager hTTPTaskManager = new HTTPTaskManager();
        String exp = "[Эпик 1 написать спринт 7, id=3, Статус=NEW приступить к выполнению 2022-03-26T10:30 ," +
                "продолжительность задачи PT30M минут]";
        String temp = String.valueOf( hTTPTaskManager.getAllEpics());
        assertEquals(temp, exp);
    }




}
