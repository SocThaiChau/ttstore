package com.example.back_end.repository;

import com.example.back_end.model.entity.Role;
import com.example.back_end.model.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoles(Roles role);
}
