package com.example.todo.controller;

import com.example.todo.dto.AddToDoDto;
import com.example.todo.dto.ResponseDto;
import com.example.todo.dto.ToDoFilterDto;
import com.example.todo.service.ToDoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/todos")
public class ToDoController {

    private final ToDoService toDoService;

    public ToDoController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @PostMapping
    public ResponseEntity<ResponseDto<Object>> addTodo(@RequestBody AddToDoDto addToDoDto,
                                                       @RequestHeader("Authorization") String token) {
        System.out.println("Token: " + token);
        ResponseDto<Object> response = toDoService.addToDo(addToDoDto, token);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<Object>> editTodo(@PathVariable Long id,
                                                        @RequestBody AddToDoDto updatedDto,
                                                        @RequestHeader("Authorization") String token) {
        ResponseDto<Object> response = toDoService.editToDo(id, updatedDto, token);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Object>> deleteTodo(@PathVariable Long id,
                                                          @RequestHeader("Authorization") String token) {
        System.out.println( "Hello from delete");
        ResponseDto<Object> response = toDoService.deleteToDo(id, token);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/filter")
    public ResponseEntity<ResponseDto<Object>> filterTodos(
            @RequestBody ToDoFilterDto filterDto,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(toDoService.getTodos(filterDto, token));
    }
}
