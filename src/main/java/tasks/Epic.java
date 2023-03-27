package main.java.tasks;

import main.java.service.Status;
import main.java.service.TaskType;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Epic extends Task {
    /*
Всем привет! а у вас у всех классы Task, Subtask и Epic не имеют ссылок друг на друга? Вчера жестко потратил 2ч на то что gson пытался сделать бесконечный паровозик из моих классов Subtask и Epic, так как у Epic есть массив Subtask'ов и у Subtask есть ссылка на Epic. Как я понял, gson пытался сериализовать Epic, в котором есть Subtask'и, а у него в свою очередь ссылка Epic, и тут начинается бесконечность ) только слово transient разорвало паровоз
 */
    private transient List<UUID> subtasks;

    public Epic(TaskType taskType, String name, Status status, String description, List<UUID> subtasks) {
        super(taskType, name, status, description);
        this.subtasks = subtasks;
    }

    public Epic(UUID id,  TaskType taskType, String name, Status status, String description, List<UUID> subtasks) {
        super(id, taskType, name, status, description);
        this.subtasks = subtasks;
    }


    @Override
    public List<UUID> getSubtasks() {
        return subtasks;
    }
    public void setSubtasks(List<UUID> subtasks) {
        this.subtasks = subtasks;
    }

    public void cleanSubtaskIds() {
        subtasks.clear();
    }
    public void removeSubtask(UUID id) {
        subtasks.remove(subtasks.indexOf(id));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }

    @Override
    public String toString() {
        return "Epic{" + "id=" + getId() + ", taskType=" + getTaskType() + ", name='" + getName()
                + '\'' + ", description='" + getDescription() + '\'' + ", status='" + getStatus() + '\'' + ", subtasksList=" + subtasks + '}';
    }

    @Override
    public String toCsvFormat() {
        String result;
        result = getId() + "," + getTaskType() + "," + getName() + "," + getStatus() + "," + getDescription();
        return result;
    }
}
