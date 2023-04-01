package main.java.tasks;

import main.java.service.Status;
import main.java.service.TaskType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Subtask extends Task {
    private UUID epicId;

    public Subtask(TaskType taskType,
                   String name,
                   String description,
                   Status status,
                   UUID epicId,
                   LocalDateTime startTime,
                   int duration) {
        super(taskType, name, description, status, startTime, duration);
        this.epicId = epicId;
    }
    public Subtask(TaskType taskType,
                   String name,
                   String description,
                   Status status,
                   LocalDateTime startTime,
                   int duration) {
        super(taskType, name, description, status, startTime, duration);
    }

    public Subtask(UUID id,
                   TaskType taskType,
                   String name,
                   String description,
                   Status status,
                   UUID epicId,
                   LocalDateTime startTime,
                   int duration) {
        super(id, taskType, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    @Override
    public UUID getEpicId() {
        return epicId;
    }

    public void setEpicId(UUID epicId) {
        this.epicId = epicId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" + "id=" + getId() +
                ", taskType=" + getTaskType() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", startTime='" + getStartTime() + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                ", duration='" + getDuration() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", epicId=" + epicId +
                '}';
    }

    @Override
    public String toCsvFormat() {
        String result;
        result = getId() + "," +
                getTaskType() + "," +
                getName() + "," +
                getDescription() + "," +
                getStatus() + "," +
                getStartTime() + "," +
                getEndTime() + "," +
                getDuration() + "," +
                epicId;
        return result;
    }
}
