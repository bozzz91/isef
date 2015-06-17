package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.Answer;

public interface AnswerRepo extends JpaRepository<Answer, Long> {
    
}
