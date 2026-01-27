package com.angular_training.demo.controller;

import com.angular_training.demo.repository.TodoItemRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.angular_training.demo.model.TodoItem;


@RestController
@RequestMapping("/todo")
@CrossOrigin(origins = "http://localhost:4200")
public class TodoController {

    private TodoItemRepository todoItemRepository;

    public TodoController(TodoItemRepository todoItemRepository) {
        this.todoItemRepository = todoItemRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<TodoItem> findall() {
        return todoItemRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public TodoItem save(@RequestBody TodoItem todoItem) {
        return todoItemRepository.save(todoItem);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public TodoItem update(@PathVariable Long id, @RequestBody TodoItem todoItem) {
        TodoItem existing = todoItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        existing.setTitle(todoItem.getTitle());
        existing.setCompleted(todoItem.isCompleted());
        existing.setPriority(todoItem.getPriority());
        existing.setDate(todoItem.getDate());

        return todoItemRepository.save(existing);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        todoItemRepository.deleteById(id);
    }

}
