package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.desu.home.isef.entity.Banner;

import java.util.Date;
import java.util.List;

public interface BannerRepo extends JpaRepository<Banner, Long> {

	List<Banner> findByImageIsNullAndCreatedGreaterThanOrderByIdAsc(Date date);

	List<Banner> findByImageIsNotNullAndCreatedGreaterThanOrderByIdAsc(Date date);

	@Modifying
	@Query("delete from Banner b where b.created < ?1 and b.image <> null")
	int deleteImageBannersWhereCreatedLessThan(Date date);

	@Modifying
	@Query("delete from Banner b where b.created < ?1 and b.image = null")
	int deleteTextBannersWhereCreatedLessThan(Date date);
}
