package tests;



import serversAndClient.HttpTaskServer;
import serversAndClient.KVServer;
import manager.HTTPTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;

import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

    public class HTTPTaskServerTests {
        static KVServer kvServer;
        static HttpTaskServer httpTaskServer;
        static TaskManager manager;
        HttpClient client = HttpClient.newHttpClient();

        static Task task;
        static Epic epic1;
        static SubTask subTask11;
        static SubTask subTask12;
        static Epic epic2;
        static SubTask subTask21;


        @BeforeEach
        void createTasks() {
            HTTPTaskManager hTTPTaskManager = new HTTPTaskManager();
            Task task4 = new Task("Задача1", "просто первая  задача в память", TaskStatus.NEW,
                    LocalDateTime.of(2022, 8, 1, 10, 0), Duration.ofMinutes(60));
            Task task5 = new Task("Задача2", "просто вторая  задача в память", TaskStatus.NEW,
                    LocalDateTime.of(2022, 7, 1, 10, 10), Duration.ofMinutes(90));
            hTTPTaskManager.addTask(task4);
            hTTPTaskManager.addTask(task5);
        }

        @Test
       void GETTasks() throws IOException, InterruptedException {
            String taskJson = "[{\"title\":\"Задача1\",\"description\":\"просто первая  задача в память\",\"id\":1," +
                    "\"status\":\"NEW\",\"duration\":{\"seconds\":3600,\"nanos\":0},\"startTime\":{\"date\":{\"year\"" +
                    ":2022,\"month\":8,\"day\":1},\"time\":{\"hour\":10,\"minute\":0,\"second\":0,\"nano\":0}}},{\"title\"" +
                    ":\"Задача2\",\"description\":\"просто вторая  задача в память\",\"id\":2,\"status\":\"NEW\",\"duration\"" +
                    ":{\"seconds\":5400,\"nanos\":0},\"startTime\":{\"date\":{\"year\":2022,\"month\":7,\"day\":1},\"time\"" +
                    ":{\"hour\":10,\"minute\":10,\"second\":0,\"nano\":0}}}]";

            httpTaskServer = new HttpTaskServer();
            httpTaskServer.start();
            sendRequest(URI.create("http://localhost:8080/tasks"));
           assertEquals(sendRequest(URI.create("http://localhost:8080/tasks")), taskJson);
            httpTaskServer.stop();
        }

        @Test
        void GETTaskById() throws IOException, InterruptedException {
            String taskJson = "{\"title\":\"Задача1\",\"description\":\"просто первая  задача в память\",\"id\":1," +
                    "\"status\":\"NEW\",\"duration\":{\"seconds\":3600,\"nanos\":0}," +
                    "\"startTime\":{\"date\":{\"year\":2022,\"month\":8,\"day\":1}," +
                    "\"time\":{\"hour\":10,\"minute\":0,\"second\":0,\"nano\":0}}}";

            httpTaskServer = new HttpTaskServer();
            httpTaskServer.start();
            sendRequest(URI.create("http://localhost:8080/tasks/task/?id=1"));
            assertEquals(sendRequest(URI.create("http://localhost:8080/tasks/task/?id=1")), taskJson);
            httpTaskServer.stop();
        }

        @Test
        void DeleteTaskById() throws IOException, InterruptedException {
            String taskJson = "[Задача2 просто вторая  задача в память, id=2, Статус=NEW приступить к выполнению 2022-07-01T10:10 ,продолжительность задачи PT1H30M минут]";

            httpTaskServer = new HttpTaskServer();
            httpTaskServer.start();
            HTTPTaskManager hTTPTaskManager = new HTTPTaskManager();

           sendRequestToDeleteById(URI.create("http://localhost:8080/tasks/task/?id=1"));
            System.out.println(hTTPTaskManager.getAllTasks());

            String temp = String.valueOf(hTTPTaskManager.getAllTasks());
            assertEquals(temp, taskJson);
        }

        static String sendRequest(  URI uri) throws IOException, InterruptedException {
            HttpRequest request = HttpRequest.newBuilder() // получаем экземпляр билдера
                    .GET()    // указываем HTTP-метод запроса
                    .uri(uri) // указываем адрес ресурса
                    .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола
                    .header("Accept", "text/html") // указываем заголовок Accept
                    .build(); // заканчиваем настройку и создаём ("строим") http-запрос

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);

            return  response.body();
        }

        static String sendRequestToDeleteById(  URI uri) throws IOException, InterruptedException {
            HttpRequest request = HttpRequest.newBuilder() // получаем экземпляр билдера
                    .DELETE()    // указываем HTTP-метод запроса
                    .uri(uri) // указываем адрес ресурса
                    .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола
                    .header("Accept", "text/html") // указываем заголовок Accept
                    .build(); // заканчиваем настройку и создаём ("строим") http-запрос

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);

            return  response.body();
        }
}
