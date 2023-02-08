package manager;

import serversAndClient.KvTaskClient;
import com.google.gson.Gson;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class HTTPTaskManager extends FileBackedTasksManager {

    private static Gson gson;

    private static KvTaskClient client;

    public HTTPTaskManager() {
    }

    public HTTPTaskManager(URI url) {
        client = new KvTaskClient(url);
    }

    public void save() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        gson = new Gson();
        List<Integer> historyList = new ArrayList<>();
        for (int i = 0; i < inMemoryHistoryManager.getHistory().size(); i++) {

            historyList.add(i);
        }

        String tasks = gson.toJson(taskMap);
        String subTasks = gson.toJson(subTaskMap);
        String epics = gson.toJson(epicMap);
        String history = gson.toJson(historyList);

        try {
            client.put("tasksKey", tasks);
            client.put("epicsKey", epics);
            client.put("subTasksKey", subTasks);
            client.put("historyKey", history);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void addTask(Task task) {
        if (checkStartTimeTask(task) == true) {
            task.setId(id++);
            taskMap.put(task.getId(), task);
            orderedTasksSet.add(task);
        }
    }
    @Override
    public void deleteTask(int id) {   //6. Удаление по идентификатору.
        orderedTasksSet.remove(getTask(id));
        taskMap.remove(id);
        historyManager.remove(id);
    }
    @Override
    public Task getTask(int id) {  // Получение задачи по ID
        if (taskMap.isEmpty()) {
            System.out.println("В трекере задач нет задач");
            return null;
        }
        if (!taskMap.containsKey(id)) {
            return null;
        }
        Task task = taskMap.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {  // Получение задачи по ID
        if (subTaskMap.isEmpty()) {
            System.out.println("В трекере cабзадач нет задач");
            return null;
        }
        if (!subTaskMap.containsKey(id)) {
            return null;
        }
        SubTask subtask = subTaskMap.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {  // Получение задачи по ID
        if (epicMap.isEmpty()) {
            System.out.println("В трекере эпиков нет задач");
            return null;
        }
        if (!epicMap.containsKey(id)) {
            return null;
        }
        Epic epic = epicMap.get(id);
        historyManager.add(epic);
        return epic;
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return (TreeSet<Task>) orderedTasksSet;
    }
    @Override
    public String addSubTask(SubTask subTask) {
        Integer epicIdOfSubTask = subTask.getEpicId();
        if (epicMap.containsKey(epicIdOfSubTask)) {
            int subTaskId = this.id++;
            subTask.setId(subTaskId);
            if (checkStartTimeTask(subTask) == true)
                subTaskMap.put(subTaskId, subTask);
            Epic epic = epicMap.get(epicIdOfSubTask);
            orderedTasksSet.add(subTask);
            epic.addSubTask(subTask);
            updateStatusOfEpic(epicIdOfSubTask);
            updateTimeAndDurationOfEpicTask(epic);
        } else {
            System.out.println(" Эпика с номером " + subTask.getEpicId() + " не сущствует");
        }
        return null;
    }

    @Override
    public void addEpic(Epic task) {
        if (checkStartTimeTask(task) == true)
            task.setId(id++);
        epicMap.put(task.getId(), task);
        orderedTasksSet.add(task);
    }
    @Override
    public void apdateEpic(Epic epic) {
        if (epic.getId() != 0)
            epicMap.put(epic.getId(), epic);
    }
    @Override
    public void printOllEpics() {
        for (Integer taskId : epicMap.keySet()) {
            Task task = epicMap.get(taskId);
            System.out.println(" Эпик " + task);
        }
    }
    @Override
    public List<Task> getAllSubTasks() {
        List<Task> allSubTasksList = new ArrayList<>();

        allSubTasksList.addAll(subTaskMap.values());
        return allSubTasksList;
    }

    public List<Task> getAllEpics() {
        List<Task> allEpucTasksList = new ArrayList<>();

        allEpucTasksList.addAll(epicMap.values());
        return allEpucTasksList;
    }
}






