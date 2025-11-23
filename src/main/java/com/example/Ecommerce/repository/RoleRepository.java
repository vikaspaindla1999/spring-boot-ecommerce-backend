package com.example.Ecommerce.repository;

import com.example.Ecommerce.model.AppRole;
import com.example.Ecommerce.model.Role;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {
    Optional<Role> findByRole(AppRole appRole);
}
