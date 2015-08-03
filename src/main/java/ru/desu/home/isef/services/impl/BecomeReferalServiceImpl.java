package ru.desu.home.isef.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.BecomeReferal;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.repo.BecomeReferalRepo;
import ru.desu.home.isef.repo.PersonRepo;
import ru.desu.home.isef.services.BecomeReferalService;
import ru.desu.home.isef.utils.Config;

import java.util.Calendar;
import java.util.Date;

@Service("becomeReferalService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BecomeReferalServiceImpl implements BecomeReferalService {

	@Autowired BecomeReferalRepo becomeReferalRepo;
	@Autowired PersonRepo personRepo;

    public BecomeReferal get() {
		return becomeReferalRepo.findOne(1l);
	}

	public BecomeReferal save(BecomeReferal br) {
		Person p = br.getPerson();
		p.addCash(-br.getCost());
		personRepo.save(p);
		br.setId(1l);
		return becomeReferalRepo.save(br);
	}

	public void delete(BecomeReferal obj) {
		becomeReferalRepo.delete(obj);
	}

	@Scheduled(cron = "0 0 0 * * *")
	public void resetMidnight() {
		BecomeReferal ref = get();
		if (ref != null) {
			ref.setCost(Config.BECOME_REF_COST);
			save(ref);
		}
	}

	@Scheduled(fixedDelay = 60000)
	public void reset() {
		BecomeReferal ref = get();
		if (ref != null) {
			Date refExpired;
			if (ref.getModified() != null) {
				refExpired = ref.getModified();
			} else {
				refExpired = ref.getCreated();
			}
			Calendar expire = Calendar.getInstance();
			expire.setTime(refExpired);
			expire.add(Calendar.MINUTE, 30);
			if (new Date().after(expire.getTime())) {
				delete(ref);
			}
		}
	}
}
