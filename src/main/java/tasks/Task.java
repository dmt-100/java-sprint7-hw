package main.java.tasks;

import main.java.service.Status;
import main.java.service.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Task {
    private UUID id;
    private TaskType taskType;
    private String name;
    private String description;
    private Status status;
    private int duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Task(TaskType taskType,
                String name,
                String description,
                Status status,
                LocalDateTime startTime,
                int duration) {
        this.taskType = taskType;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = startTime.plusMinutes(duration);
    }

    public Task(UUID id,
                TaskType taskType,
                String name,
                String description,
                Status status ,
                LocalDateTime startTime,
                int duration) {
        this.id = id;
        this.taskType = taskType;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(TaskType taskType,
                String name,
                String description,
                Status status) {
        this.taskType = taskType;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(UUID id,
                TaskType taskType,
                String name,
                String description,
                Status status) {
        this.id = id;
        this.taskType = taskType;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
    public TaskType getTaskType() {
        return taskType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime = startTime.plusMinutes(duration);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
//                && Objects.equals(name, task.name) &&
//                Objects.equals(description, task.description) &&
//                Objects.equals(status, task.status) &&
//                Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskType, name, description, status, startTime, endTime, duration);
    }

    @Override
    public String toString() {
        return "Task{" + "id=" + id +
                ", taskType=" + taskType +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", duration='" + duration + '\'' + '}';
    }

    public String toCsvFormat() { // Напишите метод сохранения задачи в строку (ТЗ-6)
        String result;
        result = id + "," +
                taskType + "," +
                name + "," +
                status + "," +
                description + "," +
                startTime + "," +
                endTime + "," +
                duration + ",";
        return result;
    }


// refactoring
    public List<UUID> getSubtasks() {
        List<UUID> list = new ArrayList<>();
        return list;
    }

    public UUID getEpicId() {
        return null;
    }
}