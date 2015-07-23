package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.Banner;

import java.util.Set;

public interface BannerRepo extends JpaRepository<Banner, Long> {

	Banner findFirstByImageIsNullAndIdNotIn(Set<Long> ids);

	Banner findFirstByImageIsNotNullAndIdNotIn(Set<Long> ids);
}
