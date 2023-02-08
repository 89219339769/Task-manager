package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    int id = 1;
    public static Map<Integer, Task> taskMap = new HashMap<>();
    public static Map<Integer, Epic> epicMap = new HashMap<>();
    public static Map<Integer, SubTask> subTaskMap = new HashMap<>();
    public static Set<Task> orderedTasksSet = new TreeSet<>();


    HistoryManager historyManager = new InMemoryHistoryManager();

    public TreeSet<Task> getPrioritizedTasks() {
        return (TreeSet<Task>) orderedTasksSet;
    }

    // метод проверки задач на пересечение
    public boolean checkStartTimeTask(Task checkingTask) {
        List<Task> tempList = getAllTasks();

        for (int i = 0; i < tempList.size(); i++)

            if ((checkingTask.getEndTimeTask().isAfter(tempList.get(i).getStartTimeTask())) &&
                    checkingTask.getEndTimeTask().isBefore(tempList.get(i).getEndTimeTask())) {
                System.out.println("время исполнения задачи пересекается с уже существующей задачей");
                return false;
            }
        for (int i = 0; i < tempList.size(); i++)
            if ((checkingTask.getStartTime().isAfter(tempList.get(i).getStartTime()))
                    && checkingTask.getStartTime().isBefore(tempList.get(i).getEndTimeTask())) {
                System.out.println("время исполнения задачи пересекается с уже существующей задачей");
                return false;
            }
        return true;
    }

    // метод получения задач разных типов в один список
    public List<Task> getAllTasks() {
        List<Task> allTasksList = new ArrayList<>();
        allTasksList.addAll(taskMap.values());
        allTasksList.addAll(epicMap.values());
        allTasksList.addAll(subTaskMap.values());
        return allTasksList;
    }

    public List<Task> getAllSubTasks() {
        List<Task> allSubTasksList = new ArrayList<>();

        allSubTasksList.addAll(subTaskMap.values());
        return allSubTasksList;
    }

    //  методы для создания задачи всех типов
    @Override
    public void addTask(Task task) {
        if (checkStartTimeTask(task)) {
            task.setId(id++);
            taskMap.put(task.getId(), task);
            orderedTasksSet.add(task);
        }
    }

    @Override
    public void addEpic(Epic task) {
        if (checkStartTimeTask(task))
            task.setId(id++);
        epicMap.put(task.getId(), task);
        orderedTasksSet.add(task);
    }

    @Override
    public String addSubTask(SubTask subTask) {
        Integer epicIdOfSubTask = subTask.getEpicId();
        if (epicMap.containsKey(epicIdOfSubTask)) {
            int subTaskId = this.id++;
            subTask.setId(subTaskId);
            if (checkStartTimeTask(subTask))
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

    // методы для получение списка  задач типов
    @Override
    public void printOllTasks() {
        for (Integer taskId : taskMap.keySet()) {
            Task task = taskMap.get(taskId);
            System.out.println(" Задача " + task);
        }
    }

    public void printOllEpics() {
        for (Integer taskId : epicMap.keySet()) {
            Task task = epicMap.get(taskId);
            System.out.println(" Эпик " + task);
        }
    }

    @Override
    public void printOllSubTasks() {
        for (Integer taskId : subTaskMap.keySet()) {
            Task task = subTaskMap.get(taskId);
            SubTask subtask = subTaskMap.get(taskId);
            System.out.println(" Подзадача эпика " + subtask.getEpicId() + task);
        }
    }

    // обновили задачу статус задачи поменялся на DONE
    @Override
    public void updateStatusOfTask(Task task) {
        Integer epicIdOfTask = task.getId();
        if (taskMap.containsKey(epicIdOfTask)) {
            taskMap.put(task.getId(), task);
            task.changeStatusOnDone();
        }
    }

    public void updateTimeAndDurationOfEpicTask(Epic task) {
        if (Epic.subTasks.size() > 0) {
            task.setStartTime(Epic.getStartTimeOfEpic());
            task.setDuration(Epic.getDurationOfEpic());
        }
    }

    // метод управляет статусом эпика в зависимости от подзадач
    @Override
    public void updateStatusOfEpic(int epicId) {
        Epic epic = epicMap.get(epicId);
        ArrayList<SubTask> subTasksOfEpic = (ArrayList<SubTask>) epic.getSubTasks();
        if ((subTasksOfEpic.isEmpty())) {  // true - таблица пустая
            epic.setStatus(TaskStatus.NEW);
        } else {
            int a = 0;
            int b = 0;

            for (SubTask x : subTasksOfEpic) {
                TaskStatus y = x.getStatus();
                if (y.equals(TaskStatus.NEW)) {
                    a += 1;
                } else if (y.equals(TaskStatus.DONE)) {
                    b += 1;
                }
            }
            if ((a != 0) && (a == subTasksOfEpic.size())) {
                epic.setStatus(TaskStatus.NEW);
            } else if (b != 0 && b == subTasksOfEpic.size()) {
                epic.setStatus(TaskStatus.DONE);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    // Обнвление  задач всех типов
    @Override
    public void updateTask(Task task) {
        if (task.getId() != 0)
            taskMap.put(task.getId(), task);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask.getId() != 0)
            subTaskMap.put(subTask.getId(), subTask);
    }

    @Override
    public void apdateEpic(Epic epic) {
        if (epic.getId() != 0)
            epicMap.put(epic.getId(), epic);
    }

    //  Удаление всех задач.
    @Override
    public void deleteTasks() {
        taskMap.clear();

    }

    @Override
    public void deleteSubTasks() {
        for (Epic epic : epicMap.values()) {
            epic.clearSubTask();
        }
        subTaskMap.clear();
    }

    // методы получения всех типов задач по идентификатору.
    @Override
    public Task getTask(int id) {  // Получение задачи по ID
        if (taskMap.isEmpty()) {
            System.out.println("В трекере задач нет задач");
            return null;
        }
        if (!taskMap.containsKey(id)) {
            //     System.out.println("Задачи с данным ID нет");
            return null;
        }
        Task task = taskMap.get(id);
        //  System.out.println(task);
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
            //      System.out.println("Подзадачи с данным ID нет");
            return null;
        }
        SubTask subtask = subTaskMap.get(id);
        //   System.out.println( subTasktask);
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
            //     System.out.println("Эпика с данным ID нет");
            return null;
        }
        Epic epic = epicMap.get(id);
        //      System.out.println(eppic);
        historyManager.add(epic);
        return epic;
    }

    // Удаление  задач всех типов по индификатору
    @Override
    public void deleteTask(int id) {   //6. Удаление по идентификатору.
        orderedTasksSet.remove(getTask(id));
        taskMap.remove(id);
        historyManager.remove(id);

    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epicMap.get(id);

        ArrayList<SubTask> subTasksOfEpic = (ArrayList<SubTask>) epic.getSubTasks();

        subTasksOfEpic.clear();

        orderedTasksSet.remove(epicMap.get(id));

        historyManager.remove(id);

    }

    @Override
    public void deleteSubTask(int id) {
        SubTask subTasksOfEpic = subTaskMap.get(id);
        orderedTasksSet.remove(subTaskMap.get(id));
        subTaskMap.remove(id);
        historyManager.remove(id);
        int epicIdOfSubTask = subTasksOfEpic.getEpicId();
        Epic epic = epicMap.get(epicIdOfSubTask);
        epic.delSubTask(subTasksOfEpic);
        updateStatusOfEpic(epicIdOfSubTask);

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
