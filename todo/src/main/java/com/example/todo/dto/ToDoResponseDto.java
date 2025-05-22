package com.example.todo.dto;

import com.example.todo.config.Status;
import java.util.Date;

public class ToDoResponseDto {
    private Long id;
    private String task;
    private Status status;
    private Date createdAt;
    private Date updatedAt;

    public ToDoResponseDto() {
    }

    public ToDoResponseDto(Long id, String task, Status status, Date createdAt, Date updatedAt) {
        this.id = id;
        this.task = task;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
} 