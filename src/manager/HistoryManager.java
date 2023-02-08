package manager;

import tasks.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);

    //возвращаем список просмотров
      List<Task> getHistory();
}
