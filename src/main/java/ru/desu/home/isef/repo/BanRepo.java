package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.Ban;

import java.util.List;

public interface BanRepo extends JpaRepository<Ban, Long> {

	List<Ban> findByUrl(String url);
}
