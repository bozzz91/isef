package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.desu.home.isef.entity.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {

    @Query("FROM person_role where lower(roleName) = lower(?1)")
    Role findByRoleName(String name);

}
