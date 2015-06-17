package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.Question;

public interface QuestionRepo extends JpaRepository<Question, Long> {
    
}
