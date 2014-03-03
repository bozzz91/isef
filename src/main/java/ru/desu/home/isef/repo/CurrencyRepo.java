package ru.desu.home.isef.repo;

import org.springframework.data.repository.CrudRepository;
import ru.desu.home.isef.entity.Currency;

public interface CurrencyRepo extends CrudRepository<Currency, Long> {

}
