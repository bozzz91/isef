package ru.desu.home.isef.services;

import ru.desu.home.isef.entity.Ban;

import java.util.List;

public interface BanService {
    
    List<Ban> findAll();

	List<Ban> find(String url);

    Ban save(Ban p);
}
