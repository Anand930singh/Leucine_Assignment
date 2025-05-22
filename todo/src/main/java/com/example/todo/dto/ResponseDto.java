package com.example.todo.dto;

public class ResponseDto<T> {
    private int status;
    private String message;
    private T data;

    public ResponseDto(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ResponseDto(int status, String message) {
        this(status, message, null);
    }

    // Getters and setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
