package com.example.to_do_list_fv;
import java.util.Date;
enum Urgency {
    LOW,
    MEDIUM,
    HIGH
}
public class TaskHelper {
    String Name, Description;
    Date  ToExecute, Postponed;
    Urgency TaskUrgency;
    int executed;
    public TaskHelper() {
    }
    public TaskHelper(String name, String description, Date toExecute, Date postponed, Urgency taskUrgency, int executed) {
        Name = name;
        Description = description;
        ToExecute = toExecute;
        Postponed = postponed;
        TaskUrgency = taskUrgency;
        this.executed = executed;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Date getToExecute() {
        return ToExecute;
    }

    public void setToExecute(Date toExecute) {
        ToExecute = toExecute;
    }

    public Date getPostponed() {
        return Postponed;
    }

    public void setPostponed(Date postponed) {
        Postponed = postponed;
    }

    public Urgency getTaskUrgency() {
        return TaskUrgency;
    }

    public void setTaskUrgency(Urgency taskUrgency) {
        TaskUrgency = taskUrgency;
    }

    public int getExecuted() {
        return executed;
    }

    public void setExecuted(int executed) {
        this.executed = executed;
    }
}
