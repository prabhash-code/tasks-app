package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.model.TaskDto;
import com.example.demo.model.TaskStatus;
import com.example.demo.repo.TaskRepository;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private TaskRepository repository;

    @Autowired
    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Long> createTask(@RequestBody TaskDto dto) {
        Task task = new Task(dto.getTitle());
        task.setDescription(dto.getDescription());

        Task savedTask = repository.save(task);

        return new ResponseEntity<>(savedTask.getId(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        Optional<Task> optionalTask = repository.findById(id);

        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            return ResponseEntity.ok(task.toDto());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateTask(@PathVariable Long id, @RequestBody TaskDto dto) {
        Optional<Task> optionalTask = repository.findById(id);

        if (!EnumUtils.isValidEnum(TaskStatus.class, dto.getStatus()))
            return ResponseEntity.badRequest().body("Available statuses are: CREATED, APPROVED, REJECTED, BLOCKED, DONE.");

        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setDescription(dto.getDescription());
            task.setTitle(dto.getTitle());
            task.setTaskStatus(TaskStatus.valueOf(dto.getStatus()));

            Task updatedTask = repository.save(task);

            return ResponseEntity.ok(updatedTask.toDto());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        Optional<Task> optionalTask = repository.findById(id);

        if (optionalTask.isPresent()) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping
    public List<TaskDto> getAllTasks() {
        List<Task> tasks = (List<Task>) repository.findAll();
        return tasks.stream().map(Task::toDto).collect(Collectors.toList());
    }
}
