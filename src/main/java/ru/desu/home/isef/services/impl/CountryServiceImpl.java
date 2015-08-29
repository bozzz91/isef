package ru.desu.home.isef.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.Country;
import ru.desu.home.isef.repo.CountryRepo;
import ru.desu.home.isef.services.CountryService;

import java.util.List;

@Service("countryService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CountryServiceImpl implements CountryService {

    @Autowired CountryRepo dao;

    @Override
    public Country find(String code) {
        return dao.findByCode(code);
    }

    @Override
    public Country save(Country b) {
        return dao.save(b);
    }

	@Override
	public void delete(Country ban) {
		dao.delete(ban);
	}

	@Override
    public List<Country> findAll() {
        return dao.findAll();
    }
}
