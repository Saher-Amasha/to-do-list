package com.example.to_do_list_fv;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

enum Urgency {
    LOW,
    MEDIUM,
    HIGH
}

public class TaskHelper {

    String Name, Description;
    Date ToExecute, Postponed;
    String Category;
    Urgency TaskUrgency;
    int executed;

    public TaskHelper() {
        Name = "Empty Task";
        Description = "Empty Description";
        ToExecute = null;
        Postponed = null;
        Category = "Empty Category";
        this.executed = 0;
        this.TaskUrgency = Urgency.LOW;
    }

    public TaskHelper(String name, String description, Date toExecute, Date postponed, int executed, String taskCategory, Urgency taskUrgency) {
        Name = name;
        Description = description;
        ToExecute = toExecute;
        Postponed = postponed;
        Category = taskCategory;
        this.executed = executed;
        this.TaskUrgency = taskUrgency;
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

    public int getExecuted() {
        return executed;
    }

    public void setExecuted(int executed) {
        this.executed = executed;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public Urgency getTaskUrgency() {
        return TaskUrgency;
    }

    public void setTaskUrgency(Urgency taskUrgency) {
        TaskUrgency = taskUrgency;
    }


}
