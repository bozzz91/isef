package ru.desu.home.isef.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.*;
import ru.desu.home.isef.entity.Role.Roles;
import ru.desu.home.isef.repo.PersonRepo;
import ru.desu.home.isef.repo.PersonWalletRepo;
import ru.desu.home.isef.repo.RatingRepo;
import ru.desu.home.isef.repo.RoleRepo;
import ru.desu.home.isef.services.PersonService;

import java.util.List;

@Service("personService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PersonServiceImpl implements PersonService {

    @Autowired PersonRepo dao;
    @Autowired RoleRepo roleDao;
    @Autowired PersonWalletRepo walletRepo;
    @Autowired RatingRepo ratingRepo;

    @Override
    public Person find(String email) {
        return dao.findByEmail(email);
    }

    @Override
    public Person save(Person p) {
        return dao.save(p);
    }

    @Override
    public void delete(Person p) {
        dao.delete(p);
    }

    @Override
    public Person findById(long id) {
        return dao.findOne(id);
    }

    @Override
    public Role findRole(Roles r) {
        return roleDao.findByRoleName(r.toString());
    }

    @Override
    public Person findByRefCode(String value) {
        return dao.findByReferalLink(value);
    }

    @Override
    public Person saveWithWallets(Person p, Iterable<PersonWallet> pw) {
        Person saved = dao.save(p);
        walletRepo.delete(pw);
        return saved;
    }

    @Override
    public Payment getLastPayment(Person p) {
        return dao.findLastPayment(p);
    }

    @Override
    public List<Person> findAll() {
        return dao.findAll(new Sort(Sort.Direction.ASC, "creationTime"));
    }

    @Override
    public Rating getRating(Person p) {
        List<Rating> ratings = ratingRepo.findAll(new Sort(Sort.Direction.DESC, "points"));
        for (Rating rating : ratings) {
            if (p.getRating() >= rating.getPoints()) {
                return rating;
            }
        }
        return ratings.get(ratings.size()-1);
    }

    @Override
    public Rating getNextRating(Rating current) {
        List<Rating> ratings = ratingRepo.findAll(new Sort(Sort.Direction.ASC, "points"));
        for (Rating nextRating : ratings) {
            if (current.getPoints() < nextRating.getPoints()) {
                return nextRating;
            }
        }
        return null;
    }

    @Override
    public Person findAdmin() {
        Person admin = dao.findOne(1l);
        return admin;
    }

    @Override
    public List<Person> findTop(int count) {
		Person admin = findAdmin();
		Role adminRole = admin.getRole();
		Page<Person> persons = dao.findByRoleNot(adminRole, new PageRequest(0, 10, new Sort(
                new Sort.Order(Sort.Direction.DESC, "rating"), 
                new Sort.Order(Sort.Direction.ASC, "creationTime"))
        ));
		return persons.getContent();
    }
}
