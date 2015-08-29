package ru.desu.home.isef.services;

import ru.desu.home.isef.entity.Country;

import java.util.List;

public interface CountryService {
    
    List<Country> findAll();

	Country find(String code);

	Country save(Country p);

	void delete(Country vountry);
}
