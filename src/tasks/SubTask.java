package tasks;

import manager.Type;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    int epicId;

    public SubTask(String title, String description, TaskStatus status, int epicId, LocalDateTime startTime, Duration duration) {
        super(title, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }


    public LocalDateTime getEndTimeSubtask() {
        LocalDateTime endTime = startTime.plusMinutes(getDuration().toMinutes());
        return endTime;
    }

    public LocalDateTime getStartTimeSubTask() {
        return startTime;
    }
}


