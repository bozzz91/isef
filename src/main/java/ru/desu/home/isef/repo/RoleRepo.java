package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.desu.home.isef.entity.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {

    @Query("FROM #{#entityName} where lower(rolename) = lower(?1)")
    public Role findByRoleName(String name);

}
