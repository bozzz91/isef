package ru.desu.home.isef.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.Captcha;
import ru.desu.home.isef.repo.CaptchaRepo;
import ru.desu.home.isef.services.CaptchaService;

import java.util.List;
import java.util.Random;

@Service("captchaService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CaptchaServiceImpl implements CaptchaService {

	@Autowired CaptchaRepo dao;

	@Override
    public Captcha findRandom() {
		List<Captcha> list = dao.findAll();
		int size = list.size();
		if (size == 0) {
			return null;
		}
		return list.get(new Random().nextInt(size));
	}
	
}
