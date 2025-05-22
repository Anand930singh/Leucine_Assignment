package com.example.todo.service;

import com.example.todo.dto.AddToDoDto;
import com.example.todo.dto.ResponseDto;
import com.example.todo.dto.ToDoFilterDto;
import com.example.todo.dto.ToDoResponseDto;
import com.example.todo.entity.Todo;
import com.example.todo.entity.Users;
import com.example.todo.repository.ToDoRepository;
import com.example.todo.specification.ToDoSpecification;
import com.example.todo.utils.JWTUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ToDoService {

    private final ToDoRepository toDoRepository;
    private final JWTUtils jwtUtils;

    public ToDoService(ToDoRepository toDoRepository, JWTUtils jwtUtils) {
        this.toDoRepository = toDoRepository;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public ResponseDto<Object> addToDo(AddToDoDto addToDoDto, String token) {
        try {
            String rawToken = extractToken(token);
            Optional<Users> userOptional = jwtUtils.getUsernameFromToken(rawToken);
            if (userOptional.isEmpty()) {
                return new ResponseDto<>(404, "User not found");
            }

            Users user = userOptional.get();

            Todo todo = new Todo();
            todo.setTask(addToDoDto.getTask());
            todo.setStatus(addToDoDto.getStatus());
            todo.setUser(user);

            toDoRepository.save(todo);

            return new ResponseDto<>(201, "Todo added successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto<>(500, "Error adding todo: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseDto<Object> editToDo(Long todoId, AddToDoDto updatedDto, String token) {
        try {
            String rawToken = extractToken(token);
            Optional<Users> userOptional = jwtUtils.getUsernameFromToken(rawToken);
            if (userOptional.isEmpty()) {
                return new ResponseDto<>(404, "User not found");
            }

            Users user = userOptional.get();

            Optional<Todo> todoOptional = toDoRepository.findById(todoId);
            if (todoOptional.isEmpty() || !todoOptional.get().getUser().getId().equals(user.getId())) {
                return new ResponseDto<>(403, "Todo not found or access denied");
            }

            Todo todo = todoOptional.get();
            todo.setTask(updatedDto.getTask());
            todo.setStatus(updatedDto.getStatus());

            toDoRepository.save(todo);
            System.out.println("Updated Successfully");
            return new ResponseDto<>(200, "Todo updated successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto<>(500, "Error updating todo: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseDto<Object> deleteToDo(Long todoId, String token) {
        try {
            String rawToken = extractToken(token);
            Optional<Users> userOptional = jwtUtils.getUsernameFromToken(rawToken);
            if (userOptional.isEmpty()) {
                return new ResponseDto<>(404, "User not found");
            }

            Users user = userOptional.get();

            Optional<Todo> todoOptional = toDoRepository.findById(todoId);
            if (todoOptional.isEmpty() || !todoOptional.get().getUser().getId().equals(user.getId())) {
                return new ResponseDto<>(403, "Todo not found or access denied");
            }

            toDoRepository.delete(todoOptional.get());

            return new ResponseDto<>(200, "Todo deleted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto<>(500, "Error deleting todo: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ResponseDto<Object> getTodos(ToDoFilterDto filterDto, String token) {
        try {
            String rawToken = extractToken(token);
            Optional<Users> userOptional = jwtUtils.getUsernameFromToken(rawToken);
            if (userOptional.isEmpty()) {
                return new ResponseDto<>(404, "User not found");
            }

            Users user = userOptional.get();

            Specification<Todo> spec = Specification.where(ToDoSpecification.belongsToUser(user));

            if (filterDto.getTask() != null && !filterDto.getTask().isEmpty()) {
                spec = spec.and(ToDoSpecification.taskContains(filterDto.getTask()));
            }

            if (filterDto.getStatus() != null) {
                spec = spec.and(ToDoSpecification.hasStatus(filterDto.getStatus()));
            }

            if (filterDto.getCreatedAt() != null) {
                spec = spec.and(ToDoSpecification.createdAt(filterDto.getCreatedAt()));
            }

            if (filterDto.getUpdatedAt() != null) {
                spec = spec.and(ToDoSpecification.updatedAt(filterDto.getUpdatedAt()));
            }


            PageRequest pageRequest = PageRequest.of(filterDto.getPage(), filterDto.getSize());
            Page<Todo> todosPage = toDoRepository.findAll(spec, pageRequest);

            List<ToDoResponseDto> todoDtos = todosPage.getContent().stream()
                    .map(todo -> new ToDoResponseDto(
                            todo.getId(),
                            todo.getTask(),
                            todo.getStatus(),
                            todo.getCreatedAt(),
                            todo.getUpdatedAt()))
                    .collect(Collectors.toList());

            Page<ToDoResponseDto> responsePage = new PageImpl<>(
                    todoDtos,
                    pageRequest,
                    todosPage.getTotalElements()
            );

            return new ResponseDto<>(200, "Todos fetched successfully", responsePage);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto<>(500, "Error fetching todos: " + e.getMessage());
        }
    }

    private String extractToken(String token) {
        return (token != null && token.startsWith("Bearer ")) ? token.substring(7) : token;
    }
}
