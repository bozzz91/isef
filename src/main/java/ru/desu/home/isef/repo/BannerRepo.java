package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.desu.home.isef.entity.Banner;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface BannerRepo extends JpaRepository<Banner, Long> {

	List<Banner> findFirstByImageIsNullAndIdNotInOrderByIdAsc(Collection<Long> ids);

	List<Banner> findByImageIsNotNullAndCreatedGreaterThanOrderByIdAsc(Date date);

	@Modifying
	@Query("delete from Banner b where b.created < ?1")
	int deleteByCreatedLessThan(Date date);
}
