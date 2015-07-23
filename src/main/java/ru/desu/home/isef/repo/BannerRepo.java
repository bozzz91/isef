package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.Banner;

import java.util.List;
import java.util.Set;

public interface BannerRepo extends JpaRepository<Banner, Long> {

	List<Banner> findFirstByImageIsNullAndIdNotInOrderByIdAsc(Set<Long> ids);

	List<Banner> findFirstByImageIsNotNullAndIdNotInOrderByIdAsc(Set<Long> ids);
}
