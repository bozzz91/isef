package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.PersonTask;
import ru.desu.home.isef.entity.PersonTaskId;

public interface PersonTaskRepo extends JpaRepository<PersonTask, PersonTaskId> {

}
