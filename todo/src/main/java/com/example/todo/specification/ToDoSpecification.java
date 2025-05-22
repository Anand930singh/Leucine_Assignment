package com.example.todo.specification;

import com.example.todo.config.Status;
import com.example.todo.entity.Todo;
import com.example.todo.entity.Users;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class ToDoSpecification {

    public static Specification<Todo> belongsToUser(Users user) {
        return (root, query, builder) -> builder.equal(root.get("user"), user);
    }

    public static Specification<Todo> taskContains(String task) {
        return (root, query, cb) ->
                task == null ? null : cb.like(cb.lower(root.get("task")), "%" + task.toLowerCase() + "%");
    }

    public static Specification<Todo> hasStatus(Status status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Todo> createdAt(Date date) {
        return (root, query, cb) ->
                date == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<Todo> updatedAt(Date date) {
        return (root, query, cb) ->
                date == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), date);
    }
}
