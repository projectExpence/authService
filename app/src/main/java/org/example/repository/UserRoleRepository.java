package org.example.repository;

import org.example.entities.Roles;
import org.example.entities.UserRole;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRoleRepository extends CrudRepository<UserRole,Long> {
    Optional<UserRole> findByRoleName(Roles roleName);
}
