package ru.desu.home.isef.services.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ParentDao {

    @PersistenceContext
    protected EntityManager em;
}
