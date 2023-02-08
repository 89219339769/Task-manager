package serversAndClient;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.HTTPTaskManager;
import manager.InMemoryHistoryManager;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import com.google.gson.Gson;

import static jdk.internal.util.xml.XMLStreamWriter.DEFAULT_CHARSET;


public class HttpTaskServer {
    final static int PORT = 8080;
    private Gson gson;
    private TaskManager manager;
    private HttpServer server;
    HTTPTaskManager hTTPTaskManager = new HTTPTaskManager();
    String[] pathSplitted;
    String path;
    HttpExchange httpExchange;
    String quary;

    public HttpTaskServer() throws IOException {
        this.manager = manager;
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", this::handleTasks);
    }

    private void handleTasks(HttpExchange httpExchange) throws IOException {
        try {
            this.httpExchange = httpExchange;
            System.out.println("\n /tasks" + httpExchange.getRequestURI());
            String requestMethod = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            String quary = httpExchange.getRequestURI().getQuery();

            pathSplitted = path.split("/");

            InputStream is = httpExchange.getRequestBody();
            try {
                String body = new String(is.readAllBytes(), DEFAULT_CHARSET);
            } catch (IOException e) {
                e.printStackTrace();
            }
            GsonBuilder gbuilder = new GsonBuilder();
            gson = gbuilder.create();

            switch (requestMethod) {

                case "GET": {
                    getHandler();
                    break;
                }
                case "POST": {
                    postHandler();
                    break;
                }

                case "DELETE": {
                    DeleteHandle();
                    break;
                }
                default: {
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println(" http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        System.out.println("Остановили сервер на порту" + PORT);
        server.stop(1);
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }


    public void getHandler() throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String quary = httpExchange.getRequestURI().getQuery();
        if (path.equals("/tasks")) {
            httpExchange.sendResponseHeaders(200, 0);
            gson = new GsonBuilder().create();
            String prioritizedJson = gson.toJson(hTTPTaskManager.getAllTasks());
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(prioritizedJson.getBytes());
            }
        }
        if (path.endsWith("/history")) {
            InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
            System.out.println("Началась обработка запроса получения истории запросов.");
            httpExchange.sendResponseHeaders(200, 0);
            String response = "История запросов:\n" + gson.toJson(inMemoryHistoryManager.getHistory());
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
        if (quary.startsWith("id=")) {
            StringBuilder sb = new StringBuilder(quary);
            sb.delete(0, 3);
            int id = Integer.parseInt(sb.toString());
            if (hTTPTaskManager.taskMap.containsKey(id)) {
                httpExchange.sendResponseHeaders(200, 0);
                Task task = hTTPTaskManager.getTask(id);
                String taskSerialized = gson.toJson(task);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(taskSerialized.getBytes());
                    httpExchange.close();
                }
                if (hTTPTaskManager.epicMap.containsKey(id)) {
                    httpExchange.sendResponseHeaders(200, 0);
                    Task epictask = hTTPTaskManager.getEpic(id);
                    String subtaskSerialized = gson.toJson(epictask);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(subtaskSerialized.getBytes());
                        httpExchange.close();
                    }
                    if (hTTPTaskManager.subTaskMap.containsKey(id)) {
                        httpExchange.sendResponseHeaders(200, 0);
                        Task subtask = hTTPTaskManager.getSubTask(id);
                        String epictaskSerialized = gson.toJson(subtask);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(epictaskSerialized.getBytes());
                            httpExchange.close();
                        }
                    } else httpExchange.sendResponseHeaders(405, 0);
                } else httpExchange.sendResponseHeaders(400, 0);
            }
        }
    }


    void postHandler() throws IOException {
        String temp = pathSplitted[2].toLowerCase();
        if (temp.equals("task")) {
            httpExchange.sendResponseHeaders(200, 0);
            InputStream is = httpExchange.getRequestBody();
            String body = new String(is.readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(body, Task.class);
            hTTPTaskManager.addTask(task);
            hTTPTaskManager.updateTask(task);
        }
        if (temp.equals("subtask")) {
            InputStream is = httpExchange.getRequestBody();
            String body = new String(is.readAllBytes(), DEFAULT_CHARSET);
            httpExchange.sendResponseHeaders(200, 0);
            SubTask subTask = gson.fromJson(body, SubTask.class);
            hTTPTaskManager.addSubTask(subTask);
            hTTPTaskManager.updateSubTask(subTask);
        }

        if (temp.equals("epic")) {
            InputStream is = httpExchange.getRequestBody();
            String body = new String(is.readAllBytes(), DEFAULT_CHARSET);
            httpExchange.sendResponseHeaders(200, 0);
            Epic epic = gson.fromJson(body, Epic.class);
            hTTPTaskManager.addEpic(epic);
            hTTPTaskManager.apdateEpic(epic);
        }
    }


    void DeleteHandle() throws IOException {
        String quary = httpExchange.getRequestURI().getQuery();
        if ( "task".equals(pathSplitted[2].toLowerCase())) {
            if (quary.startsWith("id=")){
                StringBuilder sb = new StringBuilder(quary);
                sb.delete(0, 3);
                int id = Integer.parseInt(sb.toString());
                if (hTTPTaskManager.taskMap.containsKey(id)) {
                    httpExchange.sendResponseHeaders(200, 0);
                    hTTPTaskManager.deleteTask(id);
                } else httpExchange.sendResponseHeaders(400, 0);
                httpExchange.close();
            }
        }

        if (pathSplitted[2].toLowerCase().equals("subtask")) {
            if (pathSplitted.length == 4 && pathSplitted[3].startsWith("id=")) {
                StringBuilder sb = new StringBuilder(pathSplitted[3]);
                sb.delete(0, 3);

                int id = Integer.parseInt(sb.toString());
                if (hTTPTaskManager.subTaskMap.containsKey(id)) {
                    httpExchange.sendResponseHeaders(200, 0);
                    hTTPTaskManager.deleteEpic(id);
                } else httpExchange.sendResponseHeaders(400, 0);
                httpExchange.close();
            }
        }
        if (pathSplitted[2].toLowerCase().equals("epic")) {
            if (pathSplitted.length == 4 && pathSplitted[3].startsWith("id=")) {
                StringBuilder sb = new StringBuilder(pathSplitted[3]);
                sb.delete(0, 3);

                int id = Integer.parseInt(sb.toString());
                if (hTTPTaskManager.epicMap.containsKey(id)) {
                    httpExchange.sendResponseHeaders(200, 0);
                    hTTPTaskManager.deleteEpic(id);
                    System.out.println(hTTPTaskManager.taskMap);
                } else httpExchange.sendResponseHeaders(400, 0);
                httpExchange.close();
            }
        }
    }


    public static void main(String[] args) throws IOException {
        HTTPTaskManager hTTPTaskManager = new HTTPTaskManager();
        HttpTaskServer httpTaskServer = new HttpTaskServer();

        Task task4 = new Task("Задача1", "просто первая  задача в память", TaskStatus.NEW,
                LocalDateTime.of(2022, 8, 1, 10, 0), Duration.ofMinutes(60));
        Task task5 = new Task("Задача2", "просто вторая  задача в память", TaskStatus.NEW,
                LocalDateTime.of(2022, 7, 1, 10, 10), Duration.ofMinutes(90));

        Task task6 = new Task("Задача3", "просто третья  задача в память", TaskStatus.NEW,
                LocalDateTime.of(2022, 7, 1, 10, 10), Duration.ofMinutes(90));
        Epic epc1 = new Epic("Эпик 1", "написать спринт 7",
                LocalDateTime.of(2022, 3, 26, 10, 30), Duration.ofMinutes(30));
        SubTask task2 = new SubTask(" Подзадача 1", " проверить статус эпика", TaskStatus.DONE, 3,
                LocalDateTime.of(2022, 8, 27, 10, 30), Duration.ofMinutes(30));

        hTTPTaskManager.addTask(task4);
        hTTPTaskManager.addTask(task5);
        hTTPTaskManager.addEpic(epc1);
        hTTPTaskManager.addSubTask(task2);

        httpTaskServer.start();
        //  httpTaskServer.stop();
    }
}












