package com.example.ejadwebapplication.Repository;

import com.example.ejadwebapplication.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserById(Integer id);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
