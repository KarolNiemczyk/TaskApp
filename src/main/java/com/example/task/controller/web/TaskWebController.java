// src/main/java/com/example/task/controller/web/TaskWebController.java
package com.example.task.controller.web;

import com.example.task.model.dto.TaskCreateDto;
import com.example.task.service.CategoryService;
import com.example.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class TaskWebController {

    private final TaskService taskService;
    private final CategoryService categoryService;

    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("/tasks")
    public String listTasks(Model model) {
        var page = taskService.getAllTasks(null, null, null, null, null,
                PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "createdAt")));
        model.addAttribute("tasks", page);
        return "tasks/list";
    }

    @GetMapping("/tasks/new")
    public String newTaskForm(Model model) {
        model.addAttribute("task", new TaskCreateDto());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "tasks/form";
    }

    @GetMapping("/tasks/{id}")
    public String editTaskForm(@PathVariable Long id, Model model) {
        var task = taskService.getTaskById(id);
        var dto = new TaskCreateDto();
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setDueDate(task.getDueDate());
        dto.setCategoryId(task.getCategoryId());
        model.addAttribute("task", dto);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "tasks/form";
    }

    @PostMapping("/tasks")
    public String createTask(@Valid @ModelAttribute("task") TaskCreateDto dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "tasks/form";
        }
        taskService.createTask(dto);
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/{id}")
    public String updateTask(@PathVariable Long id, @Valid @ModelAttribute("task") TaskCreateDto dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "tasks/form";
        }
        taskService.updateTask(id, dto);
        return "redirect:/tasks";
    }

    @PostMapping(value = "/tasks/{id}", params = "_method=DELETE")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }
}