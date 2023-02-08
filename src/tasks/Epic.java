package tasks;

import manager.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    public static List<SubTask> subTasks = new ArrayList<>();


    public Epic(String title, String description, LocalDateTime startTime, Duration duration) {
        super(title, description, TaskStatus.NEW, startTime, duration);
    }


    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
    }

    public List<SubTask> getSubTasks() {
        return (ArrayList<SubTask>) subTasks;
    }

    public void delSubTask(SubTask subTask) {

        subTasks.remove(subTask);
    }

    public void clearSubTask() {
        subTasks.clear();
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }


    public static Duration getDurationOfEpic() {
        if (subTasks.size() > 0) {
            Duration duration = Duration.ofMinutes(0);

            for (int i = 0; i < subTasks.size(); i++) {
                duration = duration.plus(subTasks.get(i).getDuration());

            }
            return duration;
        }
        System.out.println("Эпик без подзадач создавать нельзя");
        return null;
    }

    public static LocalDateTime getStartTimeOfEpic() {
        if (subTasks.size() > 0) {
            LocalDateTime startDateTime = subTasks.get(0).getStartTime();


            return startDateTime;
        }
        System.out.println("Эпик без подзадач создавать нельзя");
        return null;
    }

    public LocalDateTime getEndTimeOfEpic() {
        if (subTasks.size() > 0) {
            LocalDateTime endDateTime = subTasks.get(subTasks.size()-1).getEndTimeSubtask();


            return endDateTime;
        }
        System.out.println("Эпик без подзадач создавать нельзя");
        return null;
    }
}

