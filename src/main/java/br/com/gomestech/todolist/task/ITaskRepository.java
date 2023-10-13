package br.com.gomestech.todolist.task;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ITaskRepository extends JpaRepository<TaskModel, UUID> {
  List<TaskModel> findByUserId(UUID userId);
}
