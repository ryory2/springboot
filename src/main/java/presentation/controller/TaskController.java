package presentation.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.domain.model.Tasks;
import com.example.demo.domain.service.TasksService;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  @Autowired
  private TasksService tasksService;

  @GetMapping
  public List<Tasks> getAllTasks() {
    return tasksService.getAllTasks();
  }

  @GetMapping("/{id}")
  public Tasks getTaskById(@PathVariable Long id) {
    return tasksService.getTasksById(id);
  }
}
