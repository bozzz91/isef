package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.desu.home.isef.entity.Ban;

import java.util.List;

public interface BanRepo extends JpaRepository<Ban, Long> {

	@Query("select b from Ban b where ?1 like '%'||b.url||'%'")
	List<Ban> findByUrl(String url);
}
