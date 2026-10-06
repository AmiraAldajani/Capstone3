package com.example.ejadwebapplication.Repository;

import com.example.ejadwebapplication.Model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Admin findAdminById(Integer id);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
