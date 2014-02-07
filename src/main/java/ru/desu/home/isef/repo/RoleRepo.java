package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {

    public Role findByRolename(String name);

}
