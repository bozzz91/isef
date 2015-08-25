package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.desu.home.isef.entity.Banner;

import java.util.Date;
import java.util.List;

public interface BannerRepo extends JpaRepository<Banner, Long> {

	List<Banner> findByTypeAndCreatedGreaterThanOrderByIdAsc(Banner.Type type, Date date);

	@Modifying
	@Query("delete from Banner b where b.created < ?1 and b.type = ?2")
	int deleteBannersWhereCreatedLessThan(Date date, Banner.Type type);
}
