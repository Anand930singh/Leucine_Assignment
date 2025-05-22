package com.example.todo.dto;

import com.example.todo.config.Status;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ToDoFilterDto {

    private String task;
    private Status status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date updatedAt;

    private int page = 0;
    private int size = 10;

    // Getters and setters

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            this.status = null;
        } else {
            this.status = Status.valueOf(status.toUpperCase());
        }
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAfter) {
        this.createdAt = createdAfter;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date createdBefore) {
        this.updatedAt = createdBefore;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
