package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.Rating;

public interface RatingRepo extends JpaRepository<Rating, Long> {

}
