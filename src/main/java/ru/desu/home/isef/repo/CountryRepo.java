package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.Country;

public interface CountryRepo extends JpaRepository<Country, Long> {

	Country findByCode(String code);
}
