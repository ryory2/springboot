package com.example.demo.domain.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.domain.model.Tasks;
import com.example.demo.domain.repository.TasksRepository;

@Service
public class TasksService {
  @Autowired
  private TasksRepository tasksRepository;

  public List<Tasks> getAllTasks() {
    return tasksRepository.findAll();
  }

  public Tasks getTasksById(Long id) {
    return tasksRepository.findById(id)
        .orElse(null);
  }
}
