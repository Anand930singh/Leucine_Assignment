package com.example.todo.repository;

import com.example.todo.config.Status;
import com.example.todo.entity.Todo;
import com.example.todo.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ToDoRepository extends JpaRepository<Todo, Long>, JpaSpecificationExecutor<Todo> {
    List<Todo> findByUserAndStatus(Users user, Status status);
}
