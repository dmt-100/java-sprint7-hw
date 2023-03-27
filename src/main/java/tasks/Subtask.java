package main.java.tasks;

import main.java.service.Status;
import main.java.service.TaskType;

import java.util.Objects;
import java.util.UUID;

public class Subtask extends Task {
    private UUID epicId;

    public Subtask(TaskType taskType, String name, Status status, String description, UUID epicId) {
        super(taskType, name, status, description);
        this.epicId = epicId;
    }

    public Subtask(UUID id, TaskType taskType, String name, Status status, String description, UUID epicId) {
        super(id, taskType, name, status, description);
        this.epicId = epicId;
    }

    @Override
    public UUID getEpicId() {
        return epicId;
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
        return "Subtask{" + "id=" + getId() + ", taskType=" + getTaskType() + ", epicId=" + epicId + ", name='" + getName() + '\''
                + ", description='" + getDescription() + '\'' + ", status='" + getStatus() + '\'' + '}';
    }

    @Override
    public String toCsvFormat() {
        String result;
        result = getId() + "," + getTaskType() + "," + getName() + "," + getStatus() + "," + getDescription()
        + "," + epicId;
        return result;
    }
}
