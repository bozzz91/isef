package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.desu.home.isef.entity.Reverse;

public interface ReverseRepo extends JpaRepository<Reverse, Long> {
    @Query("select rev from Reverse rev where rev.coefficient = (select min(minRev.coefficient) from Reverse minRev)")
    Reverse findDefault();
}
