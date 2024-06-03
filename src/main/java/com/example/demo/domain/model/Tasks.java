package com.example.demo.domain.model;


import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tasks")
public class Tasks {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "task_name")
  private String taskName;

  @Column(name = "scheduled_completion_on")
  private LocalDateTime scheduledCompletionOn;

  @Column(name = "is_complete")
  private int isComplete;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = " updated_at")
  private LocalDateTime updatedAt;
}
