package com.example.task.controller.api;

import com.example.task.model.dto.TaskCreateDto;
import com.example.task.model.dto.TaskDto;
import com.example.task.model.entity.TaskStatus;
import com.example.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Zadania", description = "CRUD dla zadań")
public class TaskApiController {

    private final TaskService taskService;

    @Operation(summary = "Pobierz listę zadań z filtrami i paginacją")
    @GetMapping("/")
    public ResponseEntity<Page<TaskDto>> getTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) LocalDate dueDateBefore,
            @RequestParam(required = false) LocalDate dueDateAfter,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        page = Math.max(0, page);
        size = size <= 0 || size > 100 ? 10 : size;

        String[] sortParts = sort.split(",");
        String property = sortParts[0];
        Sort.Direction direction = sortParts.length > 1
                ? Sort.Direction.fromString(sortParts[1].toUpperCase())
                : Sort.Direction.DESC;

        String javaField = switch (property) {
            case "due_date", "dueDate"         -> "dueDate";
            case "created_at", "createdAt"     -> "createdAt";
            case "updated_at", "updatedAt"     -> "updatedAt";
            case "id", "title", "status"       -> property;
            default                            -> "createdAt";
        };

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, javaField));

        Page<TaskDto> result = taskService.getAllTasks(status, categoryId, dueDateBefore, dueDateAfter, title, pageable);
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "Pobierz zadanie po ID")
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id) {
        TaskDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Utwórz nowe zadanie")
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskCreateDto dto) {
        TaskDto created = taskService.createTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Zaktualizuj zadanie")
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @Valid @RequestBody TaskCreateDto dto) {
        TaskDto updated = taskService.updateTask(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Usuń zadanie")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}