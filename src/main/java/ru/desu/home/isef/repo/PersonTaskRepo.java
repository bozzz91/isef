package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.PersonTask;
import ru.desu.home.isef.entity.PersonTaskId;

import java.util.List;

public interface PersonTaskRepo extends JpaRepository<PersonTask, PersonTaskId> {

	List<PersonTask> findByStatus(int status);
}
