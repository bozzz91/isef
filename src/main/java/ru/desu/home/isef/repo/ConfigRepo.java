package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.Config;

import java.util.List;

public interface ConfigRepo extends JpaRepository<Config, Long> {

	List<Config> findByGroupIdOrderByOrderNumberAsc(Integer group);

	List<Config> findByGroupIdAndNameOrderByOrderNumberAsc(Integer group, String name);

	List<Config> findByCodeOrderByOrderNumberAsc(String name);
}
