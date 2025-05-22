package com.example.todo.dto;

import com.example.todo.config.Status;

public class AddToDoDto {
    private String task;
    private Status status;

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
