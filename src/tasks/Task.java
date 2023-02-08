package tasks;


import manager.Type;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task implements Comparable<Task>{

    private String title;  // имя задачи
    private String description;  // Описание задачи
    private int id = 0;
    private TaskStatus status;
    private Duration duration;
    LocalDateTime startTime;



    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Task(String title, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }



    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return title + " " + description + ", id=" + id +
                ", Статус=" + status + " приступить к выполнению " + startTime +
                " ,продолжительность задачи " + duration + " минут" ;
    }

    public TaskStatus changeStatusOnDone() {
        status = TaskStatus.DONE;
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Type getType() {
        return Type.TASK;
    }


    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTimeTask() {

        LocalDateTime endTime = startTime.plusMinutes(getDuration().toMinutes());

        return endTime;
    }

    public LocalDateTime getStartTimeTask() {

        return startTime;
    }

    @Override
    public int compareTo(Task o) {
        return this.getStartTime().compareTo(o.getStartTime());
    }
}
