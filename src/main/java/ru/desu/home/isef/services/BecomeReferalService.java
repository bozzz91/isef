package ru.desu.home.isef.services;

import ru.desu.home.isef.entity.BecomeReferal;

public interface BecomeReferalService {
    
    BecomeReferal get();

	BecomeReferal save(BecomeReferal p);

	void delete(BecomeReferal obj);
}
